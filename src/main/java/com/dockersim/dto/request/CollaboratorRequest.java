package com.dockersim.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CollaboratorRequest {

    private String email; // 초대/제거할 협업자 이메일
}