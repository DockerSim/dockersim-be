package com.dockersim.common;

import com.dockersim.exception.BusinessException;
import com.dockersim.exception.GeminiApiException;
import com.dockersim.exception.code.CommonErrorCode;
import com.dockersim.exception.code.ComposeErrorCode;
import com.dockersim.exception.code.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        ResponseCode ec = ex.getErrorCode();
        log.warn("BusinessException - code: {}, message: {}", ec.getCode(), ex.getMessage());
        return ResponseEntity
            .status(ec.getStatus())
            .body(ApiResponse.error(ec, ex.getMessage()));
    }

    @ExceptionHandler(GeminiApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleGeminiApi(GeminiApiException ex) {
        ResponseCode ec = ComposeErrorCode.GEMINI_API_CONNECTION_ERROR;
        log.error("GeminiApiException - message: {}", ex.getMessage(), ex);
        return ResponseEntity
            .status(ec.getStatus())
            .body(ApiResponse.error(ec, ex.getMessage()));
    }
    /**
     * 요청 파라미터 검증 실패 등 스프링 예외 처리 (필요 시 활성화)
     */
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
//        String detail = ex.getBindingResult().getFieldErrors().stream()
//            .map(e -> e.getField() + ": " + e.getDefaultMessage())
//            .collect(Collectors.joining("; "));
//        ErrorCode ec = CommonErrorCode.INVALID_REQUEST;
//        log.warn("Validation failed: {}", detail);
//        return ResponseEntity
//            .status(ec.getStatus())
//            .body(ApiResponse.error(ec, detail));
//    }

    /**
     * 그 외 모든 예외: 내부 서버 오류로 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        ResponseCode ec = CommonErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
            .status(ec.getStatus())
            .body(ApiResponse.error(ec));
    }
}