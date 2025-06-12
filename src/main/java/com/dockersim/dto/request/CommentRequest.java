package com.dockersim.dto.request;

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
public class CommentRequest {

    @Schema(description = "댓글이 달리는 게시글 ID", example = "1001", required = true)
    private Long boardId;

    @Schema(description = "댓글 내용", example = "좋은 글 감사합니다!", required = true)
    private String content;

    @Schema(description = "익명 여부", example = "true", defaultValue = "false")
    @Builder.Default
    private Boolean isAnonymous = false;
    
    public Boolean getIsAnonymous() {
        return isAnonymous;
    }
    
    public Long getBoardId() {
        return boardId;
    }
    
    public String getContent() {
        return content;
    }
}
