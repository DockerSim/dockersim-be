package com.dockersim.dto.response;

import java.time.LocalDateTime;
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
public class BoardResponse {

    @Schema(description = "게시글 ID", example = "1001")
    private Long id;

    @Schema(description = "시뮬레이션 ID", example = "sim42")
    private String simulationId;
    
    @Schema(description = "작성자 ID", example = "501")
    private Long userId;
    
    @Schema(description = "작성자 이름", example = "김개발")
    private String userName;

    @Schema(description = "제목", example = "JPA 게시판 만들기")
    private String title;

    @Schema(description = "내용", example = "게시판 구현을 연습해봅시다.")
    private String content;

    @Schema(description = "태그 목록", example = "[\"java\", \"spring\"]")
    private List<String> tags;

    @Schema(description = "게시글 유형", example = "SHARE")
    private String type;

    @Schema(description = "좋아요 수", example = "15")
    private int likeCount;
    
    @Schema(description = "댓글 수", example = "5")
    private int commentCount;

    @Schema(description = "작성 시각", example = "2023-06-12T15:30:00")
    private LocalDateTime createdAt;
}
