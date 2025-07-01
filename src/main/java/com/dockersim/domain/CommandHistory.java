// 이 클래스는 Docker 명령어 실행 이력을 저장하는 JPA 엔티티입니다.
// 주요 필드:
// - id : 엔티티 ID
// - command : 실행된 명령어
// - success : 성공 여부
// - result : 실행 결과 메시지
// - resourceId : 생성/조작된 리소스 ID
// - executedAt : 실행 시간

package com.dockersim.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "command_history")
public class CommandHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "command", nullable = false, columnDefinition = "TEXT")
    private String command;

    @Column(name = "success", nullable = false)
    private boolean success;

    @Column(name = "result", columnDefinition = "TEXT")
    private String result;

    @Column(name = "resource_id")
    private String resourceId;

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    // 기본 생성자
    protected CommandHistory() {
    }

    // 생성자
    public CommandHistory(String command, boolean success, String result, String resourceId) {
        this.command = command;
        this.success = success;
        this.result = result;
        this.resourceId = resourceId;
        this.executedAt = LocalDateTime.now();
    }

    // Getter/Setter 메서드들
    public Long getId() {
        return id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }
}