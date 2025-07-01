// 이 클래스는 Docker 명령어 문자열을 파싱하여 ParsedDockerCommand 객체로 변환합니다.
// 주요 메서드:
// - parse : 명령어 문자열을 파싱하여 ParsedDockerCommand 반환
// - tokenize : 명령어를 토큰으로 분리
// - determineDomain : 서브명령어를 기반으로 도메인 결정
// - parseTokens : 토큰들을 플래그, 옵션, 위치인자로 분류

package com.dockersim.parser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class DockerCommandParser {

    // 도메인 매핑
    private static final Map<String, String> COMMAND_DOMAIN_MAP = Map.ofEntries(
            Map.entry("run", "CONTAINER"),
            Map.entry("start", "CONTAINER"),
            Map.entry("stop", "CONTAINER"),
            Map.entry("restart", "CONTAINER"),
            Map.entry("rm", "CONTAINER"),
            Map.entry("ps", "CONTAINER"),
            Map.entry("exec", "CONTAINER"),
            Map.entry("pull", "IMAGE"),
            Map.entry("push", "IMAGE"),
            Map.entry("build", "IMAGE"),
            Map.entry("images", "IMAGE"),
            Map.entry("rmi", "IMAGE"),
            Map.entry("network", "NETWORK"),
            Map.entry("volume", "VOLUME"));

    public ParsedDockerCommand parse(String command) {
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("명령어는 비어있을 수 없습니다");
        }

        List<String> tokens = tokenize(command);

        if (tokens.isEmpty() || !tokens.get(0).equals("docker")) {
            throw new IllegalArgumentException("명령어는 'docker'로 시작해야 합니다");
        }

        if (tokens.size() < 2) {
            throw new IllegalArgumentException("docker 서브명령어가 필요합니다");
        }

        String subCommand = tokens.get(1);
        String domain = determineDomain(subCommand);

        return parseTokens(subCommand, tokens.subList(2, tokens.size()), domain);
    }

    private List<String> tokenize(String command) {
        List<String> tokens = new ArrayList<>();

        int i = 0;
        StringBuilder currentToken = new StringBuilder();

        while (i < command.length()) {
            char c = command.charAt(i);

            if (Character.isWhitespace(c)) {
                // 공백 처리
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
                // 공백 건너뛰기
                while (i < command.length() && Character.isWhitespace(command.charAt(i))) {
                    i++;
                }
                continue;
            } else if (c == '"') {
                // 큰따옴표 처리
                if (currentToken.length() > 0 && currentToken.charAt(currentToken.length() - 1) == '=') {
                    // --name="value" 형태
                    i++; // 따옴표 건너뛰기
                    while (i < command.length() && command.charAt(i) != '"') {
                        currentToken.append(command.charAt(i));
                        i++;
                    }
                    if (i < command.length()) {
                        i++; // 닫는 따옴표 건너뛰기
                    }
                } else {
                    // 독립적인 따옴표 처리
                    i++; // 따옴표 건너뛰기
                    StringBuilder quotedValue = new StringBuilder();
                    while (i < command.length() && command.charAt(i) != '"') {
                        quotedValue.append(command.charAt(i));
                        i++;
                    }
                    if (i < command.length()) {
                        i++; // 닫는 따옴표 건너뛰기
                    }
                    if (currentToken.length() > 0) {
                        tokens.add(currentToken.toString());
                        currentToken.setLength(0);
                    }
                    tokens.add(quotedValue.toString());
                }
            } else if (c == '\'') {
                // 작은따옴표 처리 (큰따옴표와 동일한 로직)
                if (currentToken.length() > 0 && currentToken.charAt(currentToken.length() - 1) == '=') {
                    i++; // 따옴표 건너뛰기
                    while (i < command.length() && command.charAt(i) != '\'') {
                        currentToken.append(command.charAt(i));
                        i++;
                    }
                    if (i < command.length()) {
                        i++; // 닫는 따옴표 건너뛰기
                    }
                } else {
                    i++; // 따옴표 건너뛰기
                    StringBuilder quotedValue = new StringBuilder();
                    while (i < command.length() && command.charAt(i) != '\'') {
                        quotedValue.append(command.charAt(i));
                        i++;
                    }
                    if (i < command.length()) {
                        i++; // 닫는 따옴표 건너뛰기
                    }
                    if (currentToken.length() > 0) {
                        tokens.add(currentToken.toString());
                        currentToken.setLength(0);
                    }
                    tokens.add(quotedValue.toString());
                }
            } else {
                // 일반 문자
                currentToken.append(c);
                i++;
            }
        }

        // 마지막 토큰 추가
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }

    private String determineDomain(String subCommand) {
        return COMMAND_DOMAIN_MAP.getOrDefault(subCommand, "UNKNOWN");
    }

    private ParsedDockerCommand parseTokens(String subCommand, List<String> tokens, String domain) {
        List<String> flags = new ArrayList<>();
        Map<String, List<String>> options = new HashMap<>();
        List<String> positionalArgs = new ArrayList<>();

        // 알려진 플래그들 (값을 받지 않는 옵션들)
        Set<String> knownFlags = Set.of("-d", "--detach", "--rm", "--interactive", "-i", "--tty", "-t");

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);

            if (token.startsWith("--")) {
                // 장형 옵션 처리
                if (token.contains("=")) {
                    // --key=value 형태
                    String[] parts = token.split("=", 2);
                    String key = parts[0];
                    String value = parts[1];
                    options.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
                } else if (knownFlags.contains(token)) {
                    // 알려진 플래그
                    flags.add(token);
                } else {
                    // --key value 형태인지 확인
                    if (i + 1 < tokens.size() && !tokens.get(i + 1).startsWith("-")) {
                        String key = token;
                        String value = tokens.get(++i);
                        options.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
                    } else {
                        // 알려지지 않은 플래그로 처리
                        flags.add(token);
                    }
                }
            } else if (token.startsWith("-") && !token.equals("-")) {
                // 단형 옵션 처리
                if (knownFlags.contains(token)) {
                    // 알려진 플래그
                    flags.add(token);
                } else {
                    // -key value 형태인지 확인
                    if (i + 1 < tokens.size() && !tokens.get(i + 1).startsWith("-")) {
                        String key = token;
                        String value = tokens.get(++i);
                        options.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
                    } else {
                        // 알려지지 않은 플래그로 처리
                        flags.add(token);
                    }
                }
            } else {
                // 위치 인자
                positionalArgs.add(token);
            }
        }

        return new ParsedDockerCommand(subCommand, flags, options, positionalArgs, domain);
    }
}