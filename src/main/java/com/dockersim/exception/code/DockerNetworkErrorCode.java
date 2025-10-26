package com.dockersim.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DockerNetworkErrorCode implements ResponseCode {
	NOT_FOUND_NETWORK(HttpStatus.NOT_FOUND, "N001", "네트워크 %s를 찾을 수 없습니다."),
	DUPLICATE_NETWORK_NAME(HttpStatus.BAD_REQUEST, "N002", "%s는 이미 존재하는 네트워크 입니다."),
	NETWORK_IN_USE(HttpStatus.CONFLICT, "N002", "%s는 연결된 컨테이너가 존재합니다."),
	;

	private final HttpStatus status;
	private final String code;
	private final String template;
}
