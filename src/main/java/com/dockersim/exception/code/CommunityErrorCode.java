package com.dockersim.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommunityErrorCode implements ErrorCode {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C005", "유효하지 않은 입력입니다"),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "C006", "게시글을 찾을 수 없습니다"),
    USER_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "C007", "사용자가 권한이 없습니다"),
    LIKE_ALREADY_EXISTS(HttpStatus.CONFLICT, "C008", "이미 좋아요를 누른 게시글입니다");

    private final HttpStatus status;
    private final String code;
    private final String template;
}