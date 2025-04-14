package com.dockersim.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonResponseCode implements ApiResponseCode {
    SUCCESS("SUCCESS", "요청이 성공적으로 처리되었습니다."),
    FAIL("FAIL", "요청 처리에 실패했습니다."),
    VALIDATION_ERROR("VALIDATION_ERROR", "입력값이 올바르지 않습니다."),
    UNAUTHORIZED("UNAUTHORIZED", "인증이 필요합니다."),
    FORBIDDEN("FORBIDDEN", "접근 권한이 없습니다."),
    NOT_FOUND("NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_ERROR("INTERNAL_ERROR", "서버 내부 오류가 발생했습니다.");
    private final String code;
    private final String message;
}
