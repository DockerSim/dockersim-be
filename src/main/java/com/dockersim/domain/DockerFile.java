package com.dockersim.domain;

import com.dockersim.dto.request.DockerFileRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
			name = "uk_user_dockerfile_name",
			columnNames = {"user_id", "name"}
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

	@Column(nullable = false) // need unique
	private String name;

	@Column(nullable = false)
	private String path;

	@Column(nullable = false)
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	public static DockerFile from(DockerFileRequest request, User user) {
		return DockerFile.builder()
			.name(request.getName())
			.path(request.getPath())
			.content(request.getContent())
			.build();
	}

	public void addUser(User user) {
		if (this.user != null) {
			this.user.getDockerfiles().remove(this);
		}
		this.user = user;
		this.user.getDockerfiles().add(this);
	}

	public void removeUser() {
		if (this.user != null) {
			this.user.getDockerfiles().remove(this);
			this.user = null;
		}
	}
}
