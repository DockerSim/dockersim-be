package com.dockersim.common.exception;

import com.dockersim.common.response.ApiResponse;
import com.dockersim.common.response.ApiResponseCode;
import com.dockersim.common.response.DockerParserCode;
import com.dockersim.service.DockerHelpRenderer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final DockerHelpRenderer dockerHelpRenderer;

    @ExceptionHandler(DockerCommandException.class)
    public ApiResponse<?> handleDockerCommandException(DockerCommandException e) {
        ApiResponseCode code = e.getCode();
        String guide = null;
        if (code instanceof DockerParserCode parserCode) {
            guide = switch (parserCode) {
                case MISSING_SUBCOMMAND -> dockerHelpRenderer.renderFullHelp();
                case INCOMPLETE_GROUP_COMMAND -> "예: docker container stop";
                default -> null;
            };
        }
        return ApiResponse.fail(code, guide);
    }
}
