package com.dockersim.dto.response;

import com.dockersim.entity.ImageSimulation;
import com.dockersim.entity.enums.ImageSource;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "이미지 정보")
public class ImageSimulationDto {

    @Schema(description = "이미지 ID")
    private Long id;

    @Schema(description = "시뮬레이션 ID")
    private String simulationId;

    @Schema(description = "이미지 이름", example = "nginx")
    private String name;

    @Schema(description = "이미지 태그", example = "latest")
    private String tag;

    @Schema(description = "네임스페이스")
    private String namespace;

    @Schema(description = "이미지 소스")
    private ImageSource source;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;

    public static ImageSimulationDto from(ImageSimulation entity) {
        return ImageSimulationDto.builder()
                .id(entity.getId())
                .simulationId(entity.getSimulationId())
                .name(entity.getName())
                .tag(entity.getTag())
                .namespace(entity.getNamespace())
                .source(entity.getSource())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}