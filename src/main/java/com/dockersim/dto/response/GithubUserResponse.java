package com.dockersim.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GithubUserResponse {

    private Long id;

    private String name;

    // 사용자 이메일 (비공개일 경우 null일 수 있음)
    private String email;

    @JsonProperty("avatar_url")
    private String avatarUrl;

}
