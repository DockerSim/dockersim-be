package com.dockersim.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다"),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "U002", "이미 존재하는 이메일입니다"),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "U003", "올바르지 않은 이메일 형식입니다"),
    USER_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "U004", "사용자 생성에 실패했습니다");

    private final HttpStatus status;
    private final String code;
    private final String template;
}
