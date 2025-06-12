package com.dockersim.common.exception;

import com.dockersim.common.response.ApiResponseCode;
import lombok.Getter;

@Getter
public class DockerCommandException extends RuntimeException {
    private final ApiResponseCode code;
    public DockerCommandException(ApiResponseCode code) {
        super(code.getMessage());
        this.code = code;
    }
}