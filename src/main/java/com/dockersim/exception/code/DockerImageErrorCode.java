package com.dockersim.exception.code;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DockerImageErrorCode implements ErrorCode {
    // 사용자 이미지 관련 (D00x)
    USER_IMAGE_NOT_FOUND       (HttpStatus.NOT_FOUND,             "D001", "사용자 이미지 '%s'를 찾을 수 없습니다"),
    USER_IMAGE_DATA_LOAD_FAIL  (HttpStatus.INTERNAL_SERVER_ERROR, "D002", "사용자 이미지 데이터 로드에 실패했습니다"),
    USER_IMAGE_PARSE_ERROR     (HttpStatus.BAD_REQUEST,           "D003", "사용자 이미지 JSON 파싱 오류: %s"),

    // 공식(Office) 이미지 관련 (D01x)
    OFFICE_IMAGE_NOT_FOUND     (HttpStatus.NOT_FOUND,             "D011", "공식 이미지 '%s'를 찾을 수 없습니다"),
    OFFICE_IMAGE_DATA_LOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "D012", "공식 이미지 데이터 로드에 실패했습니다"),
    OFFICE_IMAGE_PARSE_ERROR   (HttpStatus.BAD_REQUEST,           "D013", "공식 이미지 JSON 파싱 오류: %s");

    private final HttpStatus status;
    private final String code;
    private final String template;
}
