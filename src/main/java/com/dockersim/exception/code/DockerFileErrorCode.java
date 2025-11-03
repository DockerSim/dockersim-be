package com.dockersim.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DockerFileErrorCode implements ResponseCode {

    INVALID_DOCKER_FILE_NAME(HttpStatus.BAD_REQUEST, "DC001", "Dockerfile 이름이 누락되었습니다"),
    DOCKER_FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "DR001", "Dockerfile을 찾을 수 없습니다"),
    INVALID_DOCKER_FILE_PATH(HttpStatus.BAD_REQUEST, "DR002", "올바른 Dockerfile 경로가 아닙니다. %s");

    private final HttpStatus status;
    private final String code;
    private final String template;
}
