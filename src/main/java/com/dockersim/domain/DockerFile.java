package com.dockersim.domain;


import com.dockersim.dto.request.DockerFileRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "docker_file",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_dockerfile_name", // 제약조건에 고유한 이름 부여 (선택사항)
            columnNames = {"user_id", "name"} // 이 두 컬럼의 조합이 고유해야 함
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DockerFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String path;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;


    public static DockerFile from(DockerFileRequest request, User user) {
        return DockerFile.builder()
            .name(request.getName())
            .path(request.getPath())
            .content(request.getContent())
            .user(user)
            .build();
    }
}
