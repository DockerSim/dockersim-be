package com.dockersim.domain;


import com.dockersim.common.IdGenerator;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "docker_volumes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"simulation_id", "name"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class DockerVolume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean anonymous;

    @Column(nullable = false)
    private LocalDateTime createAt;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "simulation_id", nullable = false)
    private Simulation simulation;
    @OneToMany(mappedBy = "volume", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContainerVolume> containerVolumes = new HashSet<>();

    public static DockerVolume from(String name, boolean anonymous, Simulation simulation) {
        return DockerVolume.builder()
            .name(anonymous ? IdGenerator.generateHexFullId() : name)
            .anonymous(anonymous)
            .simulation(simulation)
            .build();
    }

    @PrePersist
    private void onAttach() {
        this.createAt = LocalDateTime.now();
    }
}
