package com.dockersim.dto.response;

import com.dockersim.domain.CommunityComments;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostCommentResponse {

    private Long id;
    private String content;
    private String author;
    private LocalDateTime createdAt;
    private Long postId;

    // Entity를 DTO로 변환하는 생성자
    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.author = comment.getAuthor();
        this.createdAt = comment.getCreatedAt();
        this.postId = comment.getPost().getId();
    }
}
