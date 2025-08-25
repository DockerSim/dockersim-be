package com.dockersim.dto.response;

import com.dockersim.domain.SimulationCollaborator;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollaboratorResponse {

    private String userId;
    private String email;
    private String name;
    private LocalDateTime invitedAt;
    private String invitedBy;

    public static CollaboratorResponse from(SimulationCollaborator collaborator) {
        return CollaboratorResponse.builder()
            .userId(collaborator.getUser().getUserId())
            .email(collaborator.getUser().getEmail())
            .name(collaborator.getUser().getName())
            .invitedAt(collaborator.getInvitedAt())
            .invitedBy(collaborator.getInvitedBy().getUserId())
            .build();
    }
}
