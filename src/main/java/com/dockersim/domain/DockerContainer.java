package com.dockersim.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.dockersim.common.IdGenerator;
import com.dockersim.dto.request.CreateContainerRequest;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "docker_containers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DockerContainer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@Column(unique = true, nullable = false, updatable = false)
	private String hexId;

	@Column(unique = true, nullable = false, updatable = false)
	private String shortHexId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ContainerStatus status;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "simulation_id", nullable = false)
	private Simulation simulation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "image_id")
	private DockerImage baseImage;

	@OneToMany(mappedBy = "container", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ContainerVolume> containerVolumes = new HashSet<>();

	@OneToMany(mappedBy = "container", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ContainerNetwork> containerNetworks = new HashSet<>();

	private String ports;
	private String bindVolumes;
	private String envs;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column()
	private LocalDateTime startedAt;

	@Column()
	private LocalDateTime stoppedAt;

	public static DockerContainer from(CreateContainerRequest request) {
		String hexId = IdGenerator.generateHexFullId();
		return DockerContainer.builder()
			.hexId(hexId)
			.shortHexId(IdGenerator.getShortId(hexId))
			//            .baseImage(request.getBaseImage())
			.name(request.getName())
			.status(request.getStatus())
			.ports(request.getPorts())
			.bindVolumes(request.getBindVolumes())
			.envs(request.getEnvs())
			.build();
	}

	public boolean start() {
		if (this.status == ContainerStatus.CREATED ||
			this.status == ContainerStatus.EXITED) {
			this.status = ContainerStatus.RUNNING;
			this.startedAt = LocalDateTime.now();
			return true;
		}
		return false;
	}

	public boolean kill() {
		if (this.status == ContainerStatus.RUNNING || this.status == ContainerStatus.PAUSED) {
			this.status = ContainerStatus.EXITED;
			this.stoppedAt = LocalDateTime.now();
			return true;
		}
		return false;
	}

	public boolean stop() {
		if (this.status == ContainerStatus.RUNNING) {
			this.status = ContainerStatus.EXITED;
			this.stoppedAt = LocalDateTime.now();
			return true;
		}
		return false;
	}

	public boolean pause() {
		if (this.status == ContainerStatus.RUNNING) {
			this.status = ContainerStatus.PAUSED;
			return true;
		}
		return false;
	}

	public boolean unpause() {
		if (this.status == ContainerStatus.PAUSED) {
			this.status = ContainerStatus.RUNNING;
			return true;
		}
		return false;
	}

	@PrePersist
	public void onPrePersist() {
		this.createdAt = LocalDateTime.now();
	}
}
