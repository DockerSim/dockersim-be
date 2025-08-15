package com.dockersim.parser;

import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerCommandErrorCode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DockerCommandParserImpl implements DockerCommandParser {

    @Override
    public List<String> tokenize(String commandLine) {
        if (commandLine == null || commandLine.trim().isEmpty()) {
            throw new BusinessException(DockerCommandErrorCode.INVALID_DOCKER_COMMAND, commandLine);
        }

        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;

        for (int i = 0; i < commandLine.length(); i++) {
            char c = commandLine.charAt(i);

            // 1. 이스케이프 문자 처리: '\'를 만나면 다음 문자는 무조건 토큰에 추가
            if (c == '\\' && i + 1 < commandLine.length()) {
                currentToken.append(commandLine.charAt(i + 1));
                ++i;
                continue;
            }

            // 2. 따옴표 처리
            if (c == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
                continue;
            }
            if (c == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
                continue;
            }

            // 3. 공백 처리 (따옴표 밖에서만)
            if (Character.isWhitespace(c) && !inSingleQuote && !inDoubleQuote) {
                if (!currentToken.isEmpty()) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
                continue;
            }

            // 4. 나머지 모든 문자는 현재 토큰에 추가
            currentToken.append(c);
        }

        // 마지막 토큰 추가
        if (!currentToken.isEmpty()) {
            tokens.add(currentToken.toString());
        }

        // 따옴표가 닫히지 않은 경우 예외 처리
        if (inSingleQuote || inDoubleQuote) {
            throw new BusinessException(DockerCommandErrorCode.FAILED_PARSE_DOCKER_COMMAND);
        }

        return tokens;
    }
}