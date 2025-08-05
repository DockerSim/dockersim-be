package com.dockersim.domain;

/**
 * 컨테이너 상태
 */
public enum ContainerStatus {
    /**
     * 생성됨 (아직 시작되지 않음)
     */
    CREATED,

    /**
     * 실행 중
     */
    RUNNING,

    /**
     * 중지됨
     */
    EXITED,

    /**
     * 재시작 중
     */
    RESTARTING,

    /**
     * 일시정지
     */
    PAUSED,

    /**
     * 제거됨
     */
    REMOVED
}