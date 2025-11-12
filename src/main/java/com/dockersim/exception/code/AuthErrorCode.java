package com.dockersim.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ResponseCode {
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "A001", "인증되지 않은 접근입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public String getTemplate() {
        return this.message;
    }

    @Override
    public HttpStatus getStatus() { // 반환 타입을 HttpStatus로 변경
        return this.httpStatus; // httpStatus 필드를 직접 반환
    }
}
