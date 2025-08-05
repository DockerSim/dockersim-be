package com.dockersim.parser;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Docker 명령어 파서 구현체
 */
@Component
public class DockerCommandParserImpl implements DockerCommandParser {

    // 지원하는 기본 명령어들
    private static final Set<String> SUPPORTED_COMMANDS = Set.of(
            "run", "ps", "start", "stop", "restart", "rm",
            "logs", "inspect", "images", "pull", "rmi",
            "volume", "network");

    // 서브 명령어가 있는 명령어들
    private static final Set<String> SUB_COMMAND_COMMANDS = Set.of("volume", "network");

    // 불리언 플래그들 (값이 없는 옵션들)
    private static final Set<String> BOOLEAN_FLAGS = Set.of("d", "a", "all", "help", "version");

    @Override
    public ParsedDockerCommand parse(String commandLine) {
        try {
            // 1. 기본 검증
            if (commandLine == null || commandLine.trim().isEmpty()) {
                return createErrorResult(commandLine, "명령어가 비어있습니다");
            }

            // 2. 토큰화
            List<String> tokens = tokenize(commandLine.trim());

            // 3. docker 접두사 확인
            if (tokens.isEmpty() || !"docker".equals(tokens.get(0))) {
                return createErrorResult(commandLine, "docker 명령어로 시작해야 합니다");
            }

            if (tokens.size() < 2) {
                return createErrorResult(commandLine, "실행할 명령어를 입력해주세요");
            }

            // 4. 메인 명령어 추출
            String mainCommand = tokens.get(1);

            // 5. 지원되는 명령어인지 확인
            if (!isSupported(mainCommand)) {
                return createErrorResult(commandLine, "지원하지 않는 명령어입니다: " + mainCommand);
            }

            // 6. 명령어별 파싱
            return parseCommand(commandLine, tokens.subList(1, tokens.size()));

        } catch (Exception e) {
            return createErrorResult(commandLine, "명령어 파싱 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public boolean isSupported(String command) {
        return SUPPORTED_COMMANDS.contains(command);
    }

    /**
     * 명령어 문자열을 토큰으로 분리
     */
    private List<String> tokenize(String commandLine) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (char c : commandLine.toCharArray()) {
            if (c == '"' || c == '\'') {
                inQuotes = !inQuotes;
            } else if (c == ' ' && !inQuotes) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(c);
            }
        }

        if (current.length() > 0) {
            tokens.add(current.toString());
        }

        return tokens;
    }

    /**
     * 실제 명령어 파싱 (docker 제외한 부분)
     */
    private ParsedDockerCommand parseCommand(String originalCommand, List<String> tokens) {
        String mainCommand = tokens.get(0);
        ParsedDockerCommand.ParsedDockerCommandBuilder builder = ParsedDockerCommand.builder()
                .originalCommand(originalCommand)
                .command(mainCommand)
                .valid(true);

        // 서브 명령어가 있는 경우 (volume, network)
        if (SUB_COMMAND_COMMANDS.contains(mainCommand)) {
            return parseSubCommand(builder, tokens);
        }

        // 일반 명령어 파싱
        return parseRegularCommand(builder, tokens);
    }

    /**
     * 서브 명령어가 있는 명령어 파싱 (volume create, network ls 등)
     */
    private ParsedDockerCommand parseSubCommand(ParsedDockerCommand.ParsedDockerCommandBuilder builder,
            List<String> tokens) {
        if (tokens.size() < 2) {
            return createErrorResult(builder.build().getOriginalCommand(),
                    tokens.get(0) + " 명령어에는 서브 명령어가 필요합니다");
        }

        String subCommand = tokens.get(1);
        builder.subCommand(subCommand);

        // 나머지 토큰들 파싱
        List<String> remainingTokens = tokens.subList(2, tokens.size());
        return parseOptionsAndTarget(builder, remainingTokens);
    }

    /**
     * 일반 명령어 파싱 (run, ps, start 등)
     */
    private ParsedDockerCommand parseRegularCommand(ParsedDockerCommand.ParsedDockerCommandBuilder builder,
            List<String> tokens) {
        List<String> remainingTokens = tokens.subList(1, tokens.size());
        return parseOptionsAndTarget(builder, remainingTokens);
    }

    /**
     * 옵션과 대상 리소스 파싱
     */
    private ParsedDockerCommand parseOptionsAndTarget(ParsedDockerCommand.ParsedDockerCommandBuilder builder,
            List<String> tokens) {
        Map<String, String> options = new HashMap<>();
        List<String> arguments = new ArrayList<>();
        String target = null;

        // 임시로 현재 빌더 상태를 가져와서 command와 subCommand 확인
        ParsedDockerCommand tempCommand = builder.build();

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);

            if (token.startsWith("--")) {
                // 긴 옵션 (--name, --port)
                String optionName = token.substring(2);

                if (BOOLEAN_FLAGS.contains(optionName)) {
                    options.put(optionName, "true");
                } else if (i + 1 < tokens.size()) {
                    options.put(optionName, tokens.get(++i));
                } else {
                    options.put(optionName, "true");
                }
            } else if (token.startsWith("-") && token.length() > 1) {
                // 짧은 옵션 (-d, -p, -v)
                String optionName = token.substring(1);

                if (BOOLEAN_FLAGS.contains(optionName)) {
                    options.put(optionName, "true");
                } else if (i + 1 < tokens.size()) {
                    options.put(optionName, tokens.get(++i));
                } else {
                    options.put(optionName, "true");
                }
            } else {
                // 일반 인자
                arguments.add(token);

                // target 설정: 첫 번째 옵션이 아닌 인자를 target으로 설정
                if (target == null && isTargetCandidate(tempCommand.getCommand(), tempCommand.getSubCommand(), token)) {
                    target = token;
                }
            }
        }

        return builder
                .options(options)
                .arguments(arguments)
                .target(target)
                .build();
    }

    /**
     * 해당 토큰이 target이 될 수 있는지 확인
     */
    private boolean isTargetCandidate(String command, String subCommand, String token) {
        // 서브 명령어가 있는 경우
        if (subCommand != null) {
            // volume create myvolume, network create mynet
            if ("create".equals(subCommand)) {
                return true;
            }
            // volume rm myvolume, network rm mynet
            if ("rm".equals(subCommand)) {
                return true;
            }
            // volume inspect myvolume, network inspect mynet
            if ("inspect".equals(subCommand)) {
                return true;
            }
            return false;
        }

        // 일반 명령어들
        // run, pull의 경우 이미지명이 target
        if ("run".equals(command) || "pull".equals(command)) {
            return true;
        }

        // start, stop, restart, rm, logs, inspect의 경우 컨테이너명이 target
        if ("start".equals(command) || "stop".equals(command) || "restart".equals(command) ||
                "rm".equals(command) || "logs".equals(command) || "inspect".equals(command)) {
            return true;
        }

        // rmi의 경우 이미지명이 target
        if ("rmi".equals(command)) {
            return true;
        }

        return false;
    }

    /**
     * 에러 결과 생성
     */
    private ParsedDockerCommand createErrorResult(String originalCommand, String errorMessage) {
        return ParsedDockerCommand.builder()
                .originalCommand(originalCommand)
                .valid(false)
                .errorMessage(errorMessage)
                .options(Collections.emptyMap())
                .arguments(Collections.emptyList())
                .build();
    }
}