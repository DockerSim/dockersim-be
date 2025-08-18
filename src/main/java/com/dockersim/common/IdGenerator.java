package com.dockersim.common;

import java.security.SecureRandom;

public class IdGenerator {
    private static final SecureRandom random = new SecureRandom();
    private static final char[] hexChars = "0123456789abcdef".toCharArray();

    private IdGenerator() {}

    /**
     * Docker에서 일반적으로 사용하는 짧은 ID 형식(12자리)의 16진수 문자열을 생성합니다.
     * @return 12자리의 무작위 16진수 문자열
     */
    public static String generateShortId() {
        return generateHex(12);
    }

    /**
     * Docker에서 사용하는 전체 ID 형식(64자리)의 16진수 문자열을 생성합니다.
     * @return 64자리의 무작위 16진수 문자열
     */
    public static String generateFullId() {
        return generateHex(64);
    }

    /**
     * 지정된 길이의 무작위 16진수 문자열을 생성합니다.
     * @param length 생성할 문자열의 길이
     * @return 생성된 16진수 문자열
     */
    private static String generateHex(int length) {
        if (length <= 0) {
            return "";
        }

        StringBuilder hexString = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            hexString.append(hexChars[random.nextInt(hexChars.length)]);
        }
        return hexString.toString();
    }
}
