package com.dockersim.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DockerContainerErrorCode implements ResponseCode {
    CONTAINER_NAME_DUPLICATE(HttpStatus.CONFLICT, "E001", "컨테이너 이름 '%s'가 이미 사용 중입니다."),
    NOT_FOUND_CONTAINER(HttpStatus.NOT_FOUND, "E002", "컨테이너 '%s'를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String template;
}
