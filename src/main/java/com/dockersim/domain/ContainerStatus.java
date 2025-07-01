// 이 enum은 Docker 컨테이너의 상태를 정의합니다.
// CREATED : 컨테이너가 생성됨
// RUNNING : 컨테이너가 실행 중
// STOPPED : 컨테이너가 중지됨
// PAUSED : 컨테이너가 일시 정지됨

package com.dockersim.domain;

public enum ContainerStatus {
    CREATED,
    RUNNING,
    STOPPED,
    PAUSED
}