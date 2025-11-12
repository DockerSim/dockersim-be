package com.dockersim.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PostType {
    QUESTION,
    SIMULATION,
    TECHNICAL;

    @JsonCreator
    public static PostType from(String s) {
        return PostType.valueOf(s.toUpperCase());
    }
}
