package com.dockersim.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private final String code;
    private final String message;
    private final T data;

    public static <T> ApiResponse<T> success(ApiResponseCode code, T data) {
        return new ApiResponse<>(code.getCode(), code.getMessage(), data);
    }

    public static <T> ApiResponse<T> fail(ApiResponseCode code) {
        return new ApiResponse<>(code.getCode(), code.getMessage(), null);
    }
    public static <T> ApiResponse<T> fail(ApiResponseCode code, T data) {
        return new ApiResponse<>(code.getCode(), code.getMessage(), data);
    }
}