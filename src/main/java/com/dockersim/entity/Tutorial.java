package com.dockersim.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tutorials")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Tutorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer difficulty; // 1: 초급, 2: 중급, 3: 고급

    @Column(nullable = false)
    private Integer orderIndex;

    @Column(columnDefinition = "JSON")
    private String content; // JSON 형태의 튜토리얼 내용

    @Column(columnDefinition = "JSON")
    private String expectedCommands; // 예상 명령어들

    @Column(columnDefinition = "JSON")
    private String hints; // 힌트들

    private Boolean isActive = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 연관관계
    @OneToMany(mappedBy = "tutorial", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TutorialProgress> progresses = new ArrayList<>();
}