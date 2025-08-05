package com.dockersim.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 시뮬레이션 엔티티
 */
@Entity
@Table(name = "simulations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Simulation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "share_state", nullable = false)
    private ShareState shareState;

    /**
     * 시뮬레이션 소유자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 협업자 목록 (양방향 관계)
     */
    @OneToMany(mappedBy = "simulation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SimulationCollaborator> collaborators = new ArrayList<>();

    /**
     * 시뮬레이션 생성 시 사용할 생성자
     */
    public Simulation(String title, ShareState shareState, User owner) {
        this.title = title;
        this.shareState = shareState;
        this.owner = owner;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 협업자 추가
     */
    public void addCollaborator(User user, Permission permission, User invitedBy) {
        SimulationCollaborator collaborator = new SimulationCollaborator(
                this, user, permission, invitedBy);
        this.collaborators.add(collaborator);
    }

    /**
     * 협업자 제거
     */
    public void removeCollaborator(User user) {
        this.collaborators.removeIf(collaborator -> collaborator.getUser().getId().equals(user.getId()));
    }

    /**
     * 사용자가 이 시뮬레이션에 접근 권한이 있는지 확인
     */
    public boolean hasAccess(User user) {
        // 소유자인 경우
        if (this.owner.getId().equals(user.getId())) {
            return true;
        }

        // 협업자인 경우
        return this.collaborators.stream()
                .anyMatch(collaborator -> collaborator.getUser().getId().equals(user.getId()));
    }

    /**
     * 사용자가 이 시뮬레이션에 쓰기 권한이 있는지 확인
     */
    public boolean hasWriteAccess(User user) {
        // 소유자인 경우 항상 쓰기 권한 있음
        if (this.owner.getId().equals(user.getId())) {
            return true;
        }

        // 협업자인 경우 WRITE 권한 확인
        return this.collaborators.stream()
                .anyMatch(collaborator -> collaborator.getUser().getId().equals(user.getId()) &&
                        collaborator.getPermission() == Permission.WRITE);
    }

    /**
     * 사용자가 소유자인지 확인
     */
    public boolean isOwner(User user) {
        return this.owner.getId().equals(user.getId());
    }

    /**
     * 사용자가 이미 협업자인지 확인
     */
    public boolean isCollaborator(User user) {
        return this.collaborators.stream()
                .anyMatch(collaborator -> collaborator.getUser().getId().equals(user.getId()));
    }

    /**
     * 특정 사용자의 협업자 정보 조회
     */
    public SimulationCollaborator findCollaborator(User user) {
        return this.collaborators.stream()
                .filter(collaborator -> collaborator.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 공유 상태 변경
     */
    public void updateShareState(ShareState shareState) {
        this.shareState = shareState;
        this.updatedAt = LocalDateTime.now();
    }
}