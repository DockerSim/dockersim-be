package com.dockersim.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardRequest {
  @Schema(description = "연결된 시뮬레이션 ID", example = "sim42")
    private String simulationId;

    @Schema(description = "게시글 제목", example = "Spring Boot 게시판 만들기")
    private String title;

    @Schema(description = "게시글 내용", example = "JPA와 DTO 구조를 활용한 CRUD 예제입니다.")
    private String content;

    @Schema(description = "태그 목록", example = "[\"spring\", \"jpa\"]")
    private List<String> tags;

    @Schema(description = "게시글 유형", example = "QNA")
    private String type;

}
