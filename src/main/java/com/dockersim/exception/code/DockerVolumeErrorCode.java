package com.dockersim.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DockerVolumeErrorCode implements ResponseCode {
    VOLUME_NOT_FOUND(HttpStatus.NOT_FOUND, "V001", "볼륨 %s를 찾을 수 없습니다."),
    VOLUME_NAME_DUPLICATED(HttpStatus.CONFLICT, "V002", "%s는 이미 사용 중인 볼륨 이름입니다.");

    private final HttpStatus status;
    private final String code;
    private final String template;
}
