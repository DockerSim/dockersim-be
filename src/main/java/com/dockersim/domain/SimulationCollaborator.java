package com.dockersim.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Permission permission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by", nullable = false)
    private User invitedBy;

    @Column(name = "invited_at", nullable = false)
    private LocalDateTime invitedAt;

    public SimulationCollaborator(Simulation simulation, User user, Permission permission, User invitedBy) {
        this.simulation = simulation;
        this.user = user;
        this.permission = permission;
        this.invitedBy = invitedBy;
        this.invitedAt = LocalDateTime.now();
    }

    /**
     * 권한 변경
     */
    public void updatePermission(Permission permission) {
        this.permission = permission;
    }
}