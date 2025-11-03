package com.dockersim.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DockerContainerErrorCode implements ResponseCode {
	NOT_FOUND_CONTAINER(HttpStatus.NOT_FOUND, "C001", "컨테이너 '%s'를 찾을 수 없습니다."),
	CONTAINER_NOT_RUNNING(HttpStatus.BAD_REQUEST, "C002", "컨테이너 '%s'가 실행 중이 아닙니다.");

	private final HttpStatus status;
	private final String code;
	private final String template;
}
