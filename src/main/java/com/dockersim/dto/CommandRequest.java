// 이 클래스는 Docker 명령어 실행 요청을 담는 DTO입니다.
// 주요 필드:
// - command : 실행할 Docker 명령어 문자열

package com.dockersim.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Docker 명령어 실행 요청")
public class CommandRequest {

    @Schema(description = "실행할 Docker 명령어", example = "docker run -d -p 8080:80 nginx")
    private String command;

    // 기본 생성자
    public CommandRequest() {
    }

    // 생성자
    public CommandRequest(String command) {
        this.command = command;
    }

    // Getter/Setter
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return "CommandRequest{" +
                "command='" + command + '\'' +
                '}';
    }
}