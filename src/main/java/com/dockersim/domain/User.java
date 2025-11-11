package com.dockersim.domain;

import com.dockersim.common.IdGenerator;
import com.dockersim.dto.request.UserRequest;
import com.dockersim.dto.response.GithubUserResponse;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    @Column(name = "public_id", unique = true, nullable = false, updatable = false)
    private String publicId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(name = "github_id", unique = true)
    private String githubId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column
    private String roles; // "ROLE_USER,ROLE_ADMIN"

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<DockerFile> dockerfiles = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Simulation> simulations = new ArrayList<>();

    public static User fromUserRequest(UserRequest request) {
        return User.builder()
            .publicId(IdGenerator.generatePublicId())
            .name(request.getName())
            .email(request.getEmail())
            .createdAt(LocalDateTime.now())
            .roles("ROLE_USER")
            .build();
    }

    public static User fromGithub(GithubUserResponse userInfo) {
        String username = userInfo.getName() != null ? userInfo.getName() : userInfo.getLogin(); // 이름이 없으면 로그인 ID를 사용
        return User.builder()
            .publicId(IdGenerator.generatePublicId())
            .name(username) // 수정된 username 사용
            .email(userInfo.getEmail())
            .githubId(String.valueOf(userInfo.getId()))
            .createdAt(LocalDateTime.now())
            .roles("ROLE_USER")
            .build();
    }

    public List<String> getRoles() {
        if (this.roles == null || this.roles.isEmpty()) {
            return new ArrayList<>();
        }
        return List.of(this.roles.split(","));
    }

    // 이메일 업데이트를 위한 setter 추가
    public void setEmail(String email) {
        this.email = email;
    }
}
