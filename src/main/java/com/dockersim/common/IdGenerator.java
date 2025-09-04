package com.dockersim.common;

import java.security.SecureRandom;
import java.util.UUID;

public class IdGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final char[] hexChars = "0123456789abcdef".toCharArray();

    private IdGenerator() {
    }

    /**
     * Docker 에서 일반적으로 사용하는 짧은 ID 형식으로 반환합니다.
     *
     * @return 12자리의 ID 반환
     */
    public static String getShortId(String id) {
        return id.length() > 12 ? id.substring(0, 12) : id;
    }

    /**
     * 문자열 형식의 서비스 공개 UUID 사용자와 시뮬레이션 식별 용도
     */
    public static String generatePublicId() {
        return UUID.randomUUID().toString();
    }


    /**
     * Docker 에서 사용하는 ID 형식(64자리)의 16진수 문자열을 생성합니다.
     */
    public static String generateHexFullId() {
        return generateHex(64);
    }


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
