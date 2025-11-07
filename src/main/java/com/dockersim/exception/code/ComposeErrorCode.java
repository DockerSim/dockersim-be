package com.dockersim.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Docker Compose 관련 오류 코드
 */
@Getter
@AllArgsConstructor
public enum ComposeErrorCode implements ResponseCode {

    // Gemini API 관련 오류
    GEMINI_API_CONNECTION_ERROR("C001", "Gemini API 연결에 실패했습니다", HttpStatus.SERVICE_UNAVAILABLE),
    GEMINI_API_TIMEOUT("C002", "Gemini API 요청 시간이 초과되었습니다", HttpStatus.REQUEST_TIMEOUT),
    GEMINI_API_QUOTA_EXCEEDED("C003", "Gemini API 할당량을 초과했습니다", HttpStatus.TOO_MANY_REQUESTS),
    GEMINI_API_INVALID_REQUEST("C004", "Gemini API 요청 형식이 잘못되었습니다", HttpStatus.BAD_REQUEST),
    GEMINI_API_UNAUTHORIZED("C005", "Gemini API 인증에 실패했습니다", HttpStatus.UNAUTHORIZED),

    // 인프라 데이터 관련 오류
    INVALID_INFRASTRUCTURE_DATA("C010", "인프라 데이터가 유효하지 않습니다", HttpStatus.BAD_REQUEST),
    EMPTY_INFRASTRUCTURE_DATA("C011", "인프라 데이터가 비어있습니다", HttpStatus.BAD_REQUEST),
    INVALID_CONTAINER_CONFIG("C012", "컨테이너 설정이 유효하지 않습니다", HttpStatus.BAD_REQUEST),
    INVALID_NETWORK_CONFIG("C013", "네트워크 설정이 유효하지 않습니다", HttpStatus.BAD_REQUEST),
    INVALID_VOLUME_CONFIG("C014", "볼륨 설정이 유효하지 않습니다", HttpStatus.BAD_REQUEST),

    // Compose 생성 관련 오류
    COMPOSE_GENERATION_FAILED("C020", "Docker-compose 파일 생성에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    COMPOSE_PARSING_ERROR("C021", "생성된 Compose 파일 파싱에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    COMPOSE_VALIDATION_ERROR("C022", "생성된 Compose 파일 검증에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),

    // 권한 관련 오류
    COMPOSE_ACCESS_DENIED("C030", "Docker-compose 생성 권한이 없습니다", HttpStatus.FORBIDDEN),
    SIMULATION_NOT_ACCESSIBLE("C031", "시뮬레이션에 접근할 수 없습니다", HttpStatus.FORBIDDEN);

    private final String code;
    private final String template;
    private final HttpStatus httpStatus;

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String getTemplate() {
        return template;
    }
}