package com.dockersim.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DockerCommandErrorCode implements ResponseCode {
    INVALID_DOCKER_COMMAND(HttpStatus.BAD_REQUEST, "C000",
        "유효하지 않은 명령어입니다. 명령어는 'docker'로 시작해야 합니다.(%s)"),
    UNSUPPORTED_DOCKER_COMMAND(HttpStatus.BAD_REQUEST, "C001", "지원하지 않는 Docker 명령어입니다: %s"),
    FAILED_PARSE_DOCKER_COMMAND(HttpStatus.BAD_REQUEST, "C009",
        "도커 명령어 파싱에 실패했습니다. 올바른 형식의 명령어가 아닙니다"),
    FAILED_EXECUTE_DOCKER_COMMAND(HttpStatus.INTERNAL_SERVER_ERROR, "C010", "도커 명령어 실행에 실패했습니다");

    private final HttpStatus status;
    private final String code;
    private final String template;
}
