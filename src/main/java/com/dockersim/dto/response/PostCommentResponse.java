package com.dockersim.dto.response;

import com.dockersim.domain.Comments;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostCommentResponse {

    private Long id;
    private String content;
    private String author; // User의 name을 저장
    private LocalDateTime createdAt;
    private Long postId;

    // Entity를 DTO로 변환하는 생성자
    public PostCommentResponse(Comments comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.author = comment.getAuthor().getName(); // User 객체에서 name을 가져옴
        this.createdAt = comment.getCreatedAt();
        this.postId = comment.getPost().getId();
    }
}
