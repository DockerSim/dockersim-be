package com.dockersim.domain;

/**
 * Docker 이미지의 저장 위치를 나타내는 Enum
 */
public enum ImageLocation {
    LOCAL, // 로컬에만 존재하는 이미지 (사용자 빌드)
    HUB    // 원본이 원격 허브에 존재하는 이미지
}
