package com.dockersim.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 협업자 초대 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InviteCollaboratorRequest {
    private String email; // 초대할 사용자 이메일
    private String permission; // READ, WRITE
}