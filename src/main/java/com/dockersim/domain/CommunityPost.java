package com.dockersim.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;
import com.dockersim.domain.enums.PostType;


@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // 임시로 author를 Spring으로 저장. 깃허브 연동 후 변경 예정
    @Column(nullable = false)
    private String author;

    // PostType Enum을 데이터베이스에 String으로 저장
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostType type;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private int views;

    // 태그를 샆(#)으로 구분된 문자열로 저장
    @Column
    private String tags;

    // 연관관계 매핑: 댓글과 좋아요
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();

    /**
     * 게시글 생성 시 사용할 생성자
     */
    public Post(String title, String content, String author, PostType type, String tags) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.type = type;
        this.tags = tags;
        this.createdAt = LocalDateTime.now();
        this.views = 0;
    }
}
