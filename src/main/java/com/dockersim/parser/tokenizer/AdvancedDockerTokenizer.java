package com.dockersim.parser.tokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * AdvancedDockerTokenizer
 *
 * This class is responsible for parsing and tokenizing Docker-like command strings.
 * It handles quotes (single and double) and escape characters (backslashes)
 * to ensure that tokens are correctly identified as they would be in a shell-like environment.
 *
 * Example with quotes:
 * Input: docker run -e "PASSWORD=pass word" 'hello world'
 * Output: ["docker", "run", "-e", "PASSWORD=pass word", "hello world"]
 */

public class AdvancedDockerTokenizer {
    private static final char DOUBLE_QUOTE = '"';
    private static final char SINGLE_QUOTE = '\'';

    public List<String> tokenize(String command) {
        if (!isValidInput(command)) {
            return new ArrayList<>();
        }

        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean insideQuotes = false;
        boolean escaped = false;
        char quoteChar = '\0';
        char[] chars = command.toCharArray();

        for (final char c : chars) {
            if (escaped) {
                currentToken.append(c);
                escaped = false;
                continue;
            }

            if (c == '\\') {
                escaped = true;
                continue;
            }

            if (isQuoteChar(c)) {
                insideQuotes = processQuotes(c, currentToken, insideQuotes, quoteChar);
                if (insideQuotes) {
                    quoteChar = c;
                }
                continue;
            }

            if (Character.isWhitespace(c) && !insideQuotes) {
                flushToken(currentToken, tokens);
            } else {
                currentToken.append(c);
            }
        }

        flushToken(currentToken, tokens);
        return tokens;
    }

    private boolean isValidInput(String input) {
        return input != null && !input.trim().isEmpty();
    }

    private boolean processQuotes(char c, StringBuilder current, boolean insideQuotes, char quoteChar) {
        if (!insideQuotes) {
            return true; // Start a new quoted section
        } else if (c == quoteChar) {
            return false; // End of the quoted section
        } else {
            current.append(c); // Add character inside quotes
            return true;
        }
    }

    private boolean isQuoteChar(char c) {
        return c == DOUBLE_QUOTE || c == SINGLE_QUOTE;
    }

    private void flushToken(StringBuilder sb, List<String> tokens) {
        if (!sb.isEmpty()) {
            tokens.add(sb.toString());
            sb.setLength(0);
        }
    }
}