package com.dockersim.dto.response;

import com.dockersim.domain.Post;
import com.dockersim.domain.enums.PostType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private String author; // User의 name을 저장
    private PostType type;
    private LocalDateTime createdAt;
    private int views;
    private String tags;
    private int likesCount; // 좋아요 수

    // Entity를 DTO로 변환하는 생성자
    public PostResponse(Post post, int likesCount) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.author = post.getAuthor().getName(); // User 객체에서 name을 가져옴
        this.type = post.getType();
        this.createdAt = post.getCreatedAt();
        this.views = post.getViews();
        this.tags = post.getTags();
        this.likesCount = likesCount;
    }

    public static PostResponse from(Post post, int likesCount) {
        return new PostResponse(post, likesCount);
    }
}
