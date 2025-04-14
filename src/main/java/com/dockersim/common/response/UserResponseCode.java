package com.dockersim.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserResponseCode implements ApiResponseCode {
    NOT_FOUND( "USER_NOT_FOUND", "해당 사용자를 찾을 수 없습니다.")
    ;
    private final String code;
    private final String message;
}
