package com.dockersim.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentAdminResponse {

    @Schema(description = "댓글 ID", example = "2001")
    private Long id;
    
    @Schema(description = "게시글 ID", example = "1001")
    private Long boardId;

    @Schema(description = "작성자 유저 ID", example = "501")
    private Long userId;
    
    @Schema(description = "작성자 이름", example = "김개발")
    private String userName;
    
    @Schema(description = "작성자 이메일", example = "user@example.com")
    private String userEmail;

    @Schema(description = "댓글 내용", example = "관리자만 이 ID를 볼 수 있어요.")
    private String content;

    // 관리자 모드일 경우, 익명이어도 작성자 이름을 볼 수 있음
    @Schema(description = "익명 여부", example = "true")
    private Boolean isAnonymous;

    @Schema(description = "댓글 작성 시각", example = "2023-06-12T16:10:00")
    private LocalDateTime createdAt;
    
    public Boolean getIsAnonymous() {
        return isAnonymous;
    }
}
