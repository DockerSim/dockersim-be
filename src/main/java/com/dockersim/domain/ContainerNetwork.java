package com.dockersim.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = {"container_id", "network_id"})
)
@NoArgsConstructor
@Getter
public class ContainerNetwork {

	private final LocalDateTime connectedAt = LocalDateTime.now();
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	@JoinColumn(name = "container_id", nullable = false)
	private DockerContainer container;
	@ManyToOne
	@JoinColumn(name = "network_id", nullable = false)
	private DockerNetwork network;

	public ContainerNetwork(DockerContainer container, DockerNetwork network) {
		this.container = container;
		this.network = network;
	}

	public static ContainerNetwork from(DockerContainer container, DockerNetwork network) {
		return new ContainerNetwork(container, network);
	}
}
