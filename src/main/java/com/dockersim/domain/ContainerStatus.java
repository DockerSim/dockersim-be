package com.dockersim.domain;

public enum ContainerStatus {
    CREATED,  // 생성되었지만 시작되지 않은 상태
    RUNNING,  // 실행 중인 상태
    STOPPED   // 중지된 상태
}
