package com.dockersim.web;


import com.dockersim.exception.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final String code;
    private final String errorMessage;

    // 성공 응답 팩토리
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null, null);
    }

    // 실패 응답 팩토리 (기본 메시지)
    public static <T> ApiResponse<T> error(ErrorCode code) {
        return new ApiResponse<>(false, null, code.getCode(), code.getMessage());
    }

    // 실패 응답 팩토리 (커스텀 메시지)
    public static <T> ApiResponse<T> error(ErrorCode code, String message) {
        return new ApiResponse<>(false, null, code.getCode(), message);
    }
}
