package com.dockersim.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.dockersim.common.IdGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "docker_networks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class DockerNetwork {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String hexId;
	private String shortHexId;
	private String name;

	private LocalDateTime createdAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "simulation_id", nullable = false)
	private Simulation simulation;

	@OneToMany(mappedBy = "network")
	private List<ContainerNetwork> containerNetworks = new ArrayList<>();

	public static DockerNetwork from(Simulation simulation, String name) {
		String hexId = IdGenerator.generateHexFullId();
		String shortHexId = IdGenerator.getShortId(hexId);

		DockerNetwork dockerNetwork = DockerNetwork.builder()
			.hexId(hexId)
			.shortHexId(shortHexId)
			.name(name)
			.simulation(simulation)
			.createdAt(LocalDateTime.now())
			.build();

		dockerNetwork.simulation.getDockerNetworks().add(dockerNetwork);

		return dockerNetwork;
	}

	public void connect(DockerContainer container) {
		ContainerNetwork cn = new ContainerNetwork(container, this);
		containerNetworks.add(cn);
		container.getContainerNetworks().add(cn);
	}

	public void disconnect(DockerContainer container) {
		container.getContainerNetworks().removeIf(cn -> cn.getNetwork().equals(this));
		containerNetworks.removeIf(cn -> cn.getContainer().equals(container));
	}

	public String getHeader() {
		return String.format("%-25s %s", this.getShortHexId(), this.getName());
	}
}
