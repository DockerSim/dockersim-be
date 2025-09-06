package com.dockersim.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DockerImageErrorCode implements ResponseCode {
	// 사용자 이미지 관련 (D00x)
	USER_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "D001", "사용자 이미지 '%s'를 찾을 수 없습니다"),
	USER_IMAGE_DATA_LOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "D002", "사용자 이미지 데이터 로드에 실패했습니다"),
	USER_IMAGE_PARSE_ERROR(HttpStatus.BAD_REQUEST, "D003", "사용자 이미지 JSON 파싱 오류: %s"),
	INVALID_NAMESPACE(HttpStatus.BAD_REQUEST, "D004", "입력한 네임스페이스(%s)가 사용자 이름과 다릅니다."),

	// 공식(Office) 이미지 관련 (D01x)
	OFFICE_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "D011", "공식 이미지 '%s'를 찾을 수 없습니다"),
	OFFICE_IMAGE_DATA_LOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "D012",
		"공식 이미지 데이터 로드(%s)에 실패했습니다"),
	OFFICE_IMAGE_PARSE_ERROR(HttpStatus.BAD_REQUEST, "D013", "공식 이미지 JSON 파싱 오류: %s"),
	OFFICE_IMAGE_CRITICAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "D014", "공식 이미지 데이터 처리 실패"),

	// 공통 (D02x)
	IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "D021", "로컬에서 이미지 '%s'를 찾을 수 없습니다."),
	IMAGE_NOT_FOUND_IN_HUB(HttpStatus.NOT_FOUND, "D022", "원격 저장소(HUB)에서 이미지 '%s'를 찾을 수 없습니다."),
	IMAGE_AMBIGUOUS(HttpStatus.BAD_REQUEST, "D023", "이미지 '%s'가 모호하여 여러 이미지를 지칭합니다."),
	PULL_BY_ID_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "D024",
		"이미지 ID로 이미지를 pull할 수 없습니다. repository:tag 형식으로 시도해주세요."),
	PUSH_BY_ID_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "D025",
		"이미지 ID로 이미지를 push할 수 없습니다. repository:tag 형식으로 시도해주세요."),
	FAIL_CONVERT_INSPECT(HttpStatus.INTERNAL_SERVER_ERROR, "D026", "JSON 형식 변환 실패");

	private final HttpStatus status;
	private final String code;
	private final String template;
}
