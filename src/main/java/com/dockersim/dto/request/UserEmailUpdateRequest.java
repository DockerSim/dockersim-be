package com.dockersim.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserEmailUpdateRequest {
    @NotBlank(message = "이메일을 작성해주세요.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;
}
