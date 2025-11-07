package com.dockersim.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Docker-compose 생성 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Docker-compose 생성 요청")
public class ComposeGenerationRequest {

    @Schema(description = "인프라 구조 데이터")
    @Valid
    @NotNull
    private InfrastructureData infrastructureData;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "인프라 구조 데이터")
    public static class InfrastructureData {

        @Schema(description = "컨테이너 정보 목록")
        private List<ContainerInfo> containers;

        @Schema(description = "이미지 정보 목록")
        private List<ImageInfo> images;

        @Schema(description = "네트워크 정보 목록")
        private List<NetworkInfo> networks;

        @Schema(description = "볼륨 정보 목록")
        private List<VolumeInfo> volumes;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "컨테이너 정보")
    public static class ContainerInfo {

        @Schema(description = "컨테이너 이름")
        private String name;

        @Schema(description = "이미지 이름")
        private String image;

        @Schema(description = "포트 매핑", example = "[\"8080:80\", \"443:443\"]")
        private List<String> ports;

        @Schema(description = "볼륨 매핑", example = "[\"./data:/app/data\"]")
        private List<String> volumes;

        @Schema(description = "환경 변수", example = "[\"NODE_ENV=production\"]")
        private List<String> environment;

        @Schema(description = "네트워크 모드")
        private String networkMode;

        @Schema(description = "컨테이너 상태")
        private String status;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "이미지 정보")
    public static class ImageInfo {

        @Schema(description = "이미지 이름")
        private String name;

        @Schema(description = "이미지 태그")
        private String tag;

        @Schema(description = "이미지 크기")
        private String size;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "네트워크 정보")
    public static class NetworkInfo {

        @Schema(description = "네트워크 이름")
        private String name;

        @Schema(description = "드라이버")
        private String driver;

        @Schema(description = "서브넷")
        private String subnet;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "볼륨 정보")
    public static class VolumeInfo {

        @Schema(description = "볼륨 이름")
        private String name;

        @Schema(description = "드라이버")
        private String driver;

        @Schema(description = "마운트 포인트")
        private String mountpoint;
    }
}