package com.dockersim.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ResponseCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다 %s"),
    USER_EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "U002", "사용자의 이메일을 찾을 수 없습니다. %s"),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "U003", "이미 존재하는 이메일입니다 %s"),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "U004", "올바르지 않은 이메일 형식입니다 %s"),
    USER_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "U005", "사용자 생성에 실패했습니다");

    private final HttpStatus status;
    private final String code;
    private final String template;
}
