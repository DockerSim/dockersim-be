package com.dockersim.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 협업자 권한 수준 열거형
 */
@Getter
@RequiredArgsConstructor
public enum Permission {

    /**
     * 읽기 전용 권한
     */
    READ("읽기 전용"),

    /**
     * 읽기/쓰기 권한
     */
    WRITE("읽기/쓰기");

    private final String description;
}