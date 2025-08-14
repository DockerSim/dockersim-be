package com.dockersim.dto.response;

import com.dockersim.domain.CommunityPost;
import com.dockersim.domain.enums.PostType;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class PostResponseDto {

    private Long id;
    private String title;
    private String content;
    private String author;
    private PostType type;
    private int views;
    private int likesCount;
    private LocalDateTime createdAt;
    private String tags;

    // Entity를 DTO로 변환하는 생성자
    public PostResponse(Post post, int likesCount) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.author = post.getAuthor();
        this.type = post.getType();
        this.views = post.getViews();
        this.likesCount = likesCount;
        this.createdAt = post.getCreatedAt();
        this.tags = post.getTags();
    }
}
