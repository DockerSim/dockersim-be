// 이 클래스는 Docker 명령어 실행 응답을 담는 DTO입니다.
// 주요 필드:
// - success : 명령어 실행 성공 여부
// - message : 실행 결과 메시지
// - resourceId : 생성/조작된 리소스 ID
// - parsedCommand : 파싱된 명령어 정보

package com.dockersim.dto;

import com.dockersim.parser.ParsedDockerCommand;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Docker 명령어 실행 응답")
public class CommandResponse {

    @Schema(description = "실행 성공 여부")
    private boolean success;

    @Schema(description = "실행 결과 메시지")
    private String message;

    @Schema(description = "생성/조작된 리소스 ID")
    private String resourceId;

    @Schema(description = "파싱된 명령어 정보")
    private ParsedDockerCommand parsedCommand;

    // 기본 생성자
    public CommandResponse() {
    }

    // 생성자
    public CommandResponse(boolean success, String message, String resourceId, ParsedDockerCommand parsedCommand) {
        this.success = success;
        this.message = message;
        this.resourceId = resourceId;
        this.parsedCommand = parsedCommand;
    }

    // Getter/Setter
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public ParsedDockerCommand getParsedCommand() {
        return parsedCommand;
    }

    public void setParsedCommand(ParsedDockerCommand parsedCommand) {
        this.parsedCommand = parsedCommand;
    }

    @Override
    public String toString() {
        return "CommandResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", parsedCommand=" + parsedCommand +
                '}';
    }
}