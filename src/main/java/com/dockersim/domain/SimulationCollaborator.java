package com.dockersim.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 시뮬레이션 협업자 엔티티 (중간 테이블)
 */
@Entity
@Table(name = "simulation_collaborators")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulationCollaborator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id", nullable = false)
    private Simulation simulation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by", nullable = false)
    private User invitedBy;

    @Column(name = "invited_at", nullable = false)
    private LocalDateTime invitedAt;

    public SimulationCollaborator(Simulation simulation, User user, User invitedBy) {
        this.simulation = simulation;
        this.user = user;
        this.invitedBy = invitedBy;
        this.invitedAt = LocalDateTime.now();
    }
}