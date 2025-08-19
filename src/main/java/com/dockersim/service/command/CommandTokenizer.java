package com.dockersim.service.command;

import java.util.ArrayList;
import java.util.List;

public final class CommandTokenizer {
    private CommandTokenizer() {
    }

    /**
     * 공백과 따옴표를 모두 고려하여 명령어 문자열을 토큰으로 분리합니다.
     * @param input 전체 명령어 문자열
     * @return 분리된 토큰들의 리스트
     */
    public static List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        if (input == null || input.isBlank()) {
            return tokens;
        }

        StringBuilder currentToken = new StringBuilder();
        boolean inQuote = false;

        for (char c : input.toCharArray()) {
            if (c == '"') {
                inQuote = !inQuote; // 따옴표를 만나면 상태를 뒤집음
            } else if (Character.isWhitespace(c) && !inQuote) {
                // 따옴표 밖에서 공백을 만나면, 현재까지의 토큰을 리스트에 추가
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0); // 현재 토큰 초기화
                }
            } else {
                // 그 외의 모든 문자는 현재 토큰에 추가
                currentToken.append(c);
            }
        }

        // 마지막 토큰 추가
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }
}
