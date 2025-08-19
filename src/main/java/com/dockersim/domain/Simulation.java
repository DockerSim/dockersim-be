package com.dockersim.domain;

import com.dockersim.dto.request.SimulationRequest;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.SimulationErrorCode;
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
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 시뮬레이션 엔티티
 */
@Entity
@Table(name = "simulations")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Simulation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "simulation_id", unique = true, nullable = false, updatable = false)
    private UUID simulationId;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "share_state", nullable = false)
    private SimulationShareState shareState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    @OneToMany(mappedBy = "simulation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SimulationCollaborator> collaborators = new ArrayList<>();


    public static Simulation from(
        SimulationRequest request,
        SimulationShareState shareState,
        User owner
    ) {
        return Simulation.builder()
            .simulationId(UUID.randomUUID())
            .title(request.getTitle())
            .shareState(shareState)
            .owner(owner)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .collaborators(new ArrayList<>())
            .build();
    }

    public void updateTitle(String title) {
        if (title != null && !title.isEmpty()) {
            this.title = title;
        } else {
            throw new BusinessException(SimulationErrorCode.SIMULATION_TITLE_NOT_INVALID, title);
        }
    }

    public void addCollaborator(User user, User invitedBy) {
        SimulationCollaborator collaborator = new SimulationCollaborator(
            this, user, invitedBy);
        this.collaborators.add(collaborator);
    }

    public void removeCollaborator(User user) {
        this.collaborators.removeIf(
            collaborator -> collaborator.getUser().getUserId().equals(user.getUserId()));
    }

    public void removeAllCollaborators() {
        this.collaborators.clear();
    }


    /**
     * 사용자가 이 시뮬레이션에 쓰기 권한이 있는지 확인
     */
    public boolean hasWriteAccess(User user) {
        return isOwner(user) || isCollaborator(user);
    }

    public boolean isOwner(User user) {
        return this.owner.getUserId().equals(user.getUserId());
    }

    public boolean isCollaborator(User user) {
        return this.collaborators.stream()
            .anyMatch(collaborator -> collaborator.getUser().getUserId().equals(user.getUserId()));
    }

    public SimulationCollaborator findCollaborator(User user) {
        return this.collaborators.stream()
            .filter(collaborator -> collaborator.getUser().getUserId().equals(user.getUserId()))
            .findFirst()
            .orElse(null);
    }

    public void updateShareState(SimulationShareState shareState) {
        this.shareState = shareState;
        this.updatedAt = LocalDateTime.now();
    }
}