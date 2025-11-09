package com.dockersim.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TokenInfo {
    private String grantType; // ex. "Bearer"
    private String accessToken;
    private String refreshToken;
}
