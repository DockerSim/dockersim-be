package com.dockersim.dto.response;

import com.dockersim.domain.SimulationCollaborator;
import com.dockersim.util.IdConverter;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollaboratorResponse {

    private UUID id;
    private UUID userId;
    private String userName;
    private String userEmail;
    private UUID invitedById;
    private String invitedByName;
    private LocalDateTime invitedAt;

    public static CollaboratorResponse from(SimulationCollaborator collaborator) {
        return CollaboratorResponse.builder()
            .id(IdConverter.toUUID(collaborator.getId()))
            .userId(IdConverter.toUUID(collaborator.getUser().getId()))
            .userName(collaborator.getUser().getName())
            .userEmail(collaborator.getUser().getEmail())
            .invitedById(IdConverter.toUUID(collaborator.getInvitedBy().getId()))
            .invitedByName(collaborator.getInvitedBy().getName())
            .invitedAt(collaborator.getInvitedAt())
            .build();
    }
}