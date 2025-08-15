package com.dockersim.dto.response;

import com.dockersim.domain.SimulationCollaborator;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 협업자 정보 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollaboratorResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long invitedById;
    private String invitedByName;
    private LocalDateTime invitedAt;

    /**
     * SimulationCollaborator 엔티티를 DTO로 변환
     */
    public static CollaboratorResponse from(SimulationCollaborator collaborator) {
        return new CollaboratorResponse(
            collaborator.getId(),
            collaborator.getUser().getId(),
            collaborator.getUser().getName(),
            collaborator.getUser().getEmail(),
            collaborator.getInvitedBy().getId(),
            collaborator.getInvitedBy().getName(),
            collaborator.getInvitedAt());
    }
}