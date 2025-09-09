package com.dockersim.domain;

import com.dockersim.dto.request.UserRequest;
import com.dockersim.dto.response.GithubUserResponse;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "github_id", unique = true)
    private String githubId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();


    public static User fromUserRequest(UserRequest request) {
        return User.builder()
            .userId(UUID.randomUUID())
            .name(request.getName())
            .email(request.getEmail())
            .createdAt(LocalDateTime.now())
            .build();
    }

    public static User fromGithub(GithubUserResponse githubDto) {
        return User.builder()
                .userId(UUID.randomUUID())
                .githubId(String.valueOf(githubDto.getId()))
                .name(githubDto.getName())
                .email(githubDto.getEmail())
                .roles(List.of("ROLE_USER"))
                .createdAt(LocalDateTime.now())
                .build();
    }
}