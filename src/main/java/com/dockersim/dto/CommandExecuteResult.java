// 이 클래스는 Docker 명령어 실행 결과를 담는 데이터 객체입니다.
// 주요 필드:
// - success : 명령어 실행 성공 여부
// - message : 실행 결과 메시지
// - resourceId : 생성/조작된 리소스의 ID (선택적)

package com.dockersim.dto;

public class CommandExecuteResult {

    private final boolean success;
    private final String message;
    private final String resourceId;

    public CommandExecuteResult(boolean success, String message) {
        this(success, message, null);
    }

    public CommandExecuteResult(boolean success, String message, String resourceId) {
        this.success = success;
        this.message = message;
        this.resourceId = resourceId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getResourceId() {
        return resourceId;
    }

    @Override
    public String toString() {
        return "CommandExecuteResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", resourceId='" + resourceId + '\'' +
                '}';
    }
}