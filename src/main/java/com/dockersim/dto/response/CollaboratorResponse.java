package com.dockersim.dto.response;

import com.dockersim.domain.SimulationCollaborator;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollaboratorResponse {

    private String userPublicId;
    private String email;
    private String name;
    private LocalDateTime invitedAt;
    private String invitedPublicIdBy;

    public static CollaboratorResponse from(SimulationCollaborator collaborator) {
        return CollaboratorResponse.builder()
            .userPublicId(collaborator.getUser().getPublicId())
            .email(collaborator.getUser().getEmail())
            .name(collaborator.getUser().getName())
            .invitedAt(collaborator.getInvitedAt())
            .invitedPublicIdBy(collaborator.getInvitedBy().getPublicId())
            .build();
    }
}
