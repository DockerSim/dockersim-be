package com.dockersim.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 시뮬레이션 공유 상태 열거형
 */
@Getter
@RequiredArgsConstructor
public enum SimulationShareState {


    /**
     * 비공개 - 소유자만 접근 가능
     */
    PRIVATE("비공개"),

    /**
     * 읽기 전용 - 협업자는 읽기만 가능
     */
    READ("읽기 전용"),

    /**
     * 읽기/쓰기 - 협업자는 읽기/쓰기 모두 가능
     */
    WRITE("읽기/쓰기");

    private final String description;
}