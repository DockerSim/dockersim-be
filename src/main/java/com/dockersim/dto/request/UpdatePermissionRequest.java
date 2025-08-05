package com.dockersim.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 협업자 권한 변경 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePermissionRequest {
    private String permission; // READ, WRITE
}