package com.dockersim.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentRequest {

    @NotBlank(message = "댓글 내용은 필수 입력 항목입니다.")
    @Size(max = 1000, message = "댓글은 1000자 이내로 작성해야 합니다.")
    private String content;

    private Long postId;

    // 댓글 작성자 나중 구현
}
