package com.dockersim.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum SuccessCode implements ResponseCode {
    COMMAND_EXECUTE(HttpStatus.OK, "RS000", "%s에 대해 %s를 수행했습니다."),
    COMMAND_EXECUTE_CREATE(HttpStatus.CREATED, "RS001", "[%s] %s를 생성했습니다."),
    COMMAND_EXECUTE_READ(HttpStatus.OK, "RS002", "[%s] %s를 조회했습니다."),
    COMMAND_EXECUTE_UPDATE(HttpStatus.OK, "RS003", "[%s] %s를 수정했습니다."),
    COMMAND_EXECUTE_DELETE(HttpStatus.NO_CONTENT, "RS0004", "[%s] %s를 삭제했습니다."),
    COMMAND_EXECUTE_PULL_IMAGE(HttpStatus.OK, "RS0005", "도커 공식 저장소에서 %s를 내려 받았습니다.");

    private final HttpStatus status;
    private final String code;
    private final String template;
}
