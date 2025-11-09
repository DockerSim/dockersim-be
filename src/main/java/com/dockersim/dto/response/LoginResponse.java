package com.dockersim.dto.response;

import lombok.Getter;

@Getter
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private boolean isAdditionalInfoRequired;

    public LoginResponse(String accessToken, String refreshToken, boolean isAdditionalInfoRequired) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.isAdditionalInfoRequired = isAdditionalInfoRequired;
    }
}
