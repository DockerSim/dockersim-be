package com.dockersim.service;

import com.dockersim.dto.ParsedDockerCommand;
import com.dockersim.dto.response.CommandExecuteResponse;
import com.dockersim.entity.ImageSimulation;
import com.dockersim.entity.enums.ImageSource;
import com.dockersim.repository.ImageSimulationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ImageSimulationService 종합 테스트")
class ImageSimulationServiceTest {

    @Mock
    private ImageSimulationRepository imageRepository;

    @Mock
    private DockerHubOfficialImageService officialImageService;

    @InjectMocks
    private ImageSimulationService imageService;

    private String simulationId;
    private ImageSimulation sampleImage;

    @BeforeEach
    void setUp() {
        simulationId = "test-simulation-001";
        sampleImage = ImageSimulation.builder()
                .name("myapp")
                .tag("v1.0")
                .namespace(null)
                .simulationId(simulationId)
                .source(ImageSource.BUILT)
                .build();
    }

    // =============================================================================
    // 1. Docker Images 테스트
    // =============================================================================

    @Test
    @DisplayName("1-1. 빈 상태에서 이미지 목록 조회")
    void testEmptyImagesList() {
        // Given
        ParsedDockerCommand command = ParsedDockerCommand.builder()
                .command("docker images")
                .subCommand("images")
                .arguments(List.of())
                .valid(true)
                .build();
        when(imageRepository.findBySimulationId(simulationId)).thenReturn(List.of());

        // When
        CommandExecuteResponse result = imageService.executeCommand(command, simulationId);

        // Then
        assertThat(result.getSuccess()).isTrue();
        assertThat(result.getOutput()).contains("REPOSITORY", "TAG", "IMAGE ID");
    }

    @Test
    @DisplayName("1-2. 이미지 목록 조회 (이미지 존재)")
    void testImagesListWithData() {
        // Given
        ParsedDockerCommand command = ParsedDockerCommand.builder()
                .command("docker images")
                .subCommand("images")
                .arguments(List.of())
                .valid(true)
                .build();

        ImageSimulation image1 = ImageSimulation.builder()
                .name("myapp").tag("v1.0")
                .simulationId(simulationId).source(ImageSource.BUILT).build();
        ImageSimulation image2 = ImageSimulation.builder()
                .name("nginx").tag("latest")
                .namespace("library").simulationId(simulationId).source(ImageSource.PULLED).build();

        when(imageRepository.findBySimulationId(simulationId)).thenReturn(Arrays.asList(image1, image2));

        // When
        CommandExecuteResponse result = imageService.executeCommand(command, simulationId);

        // Then
        assertThat(result.getSuccess()).isTrue();
        assertThat(result.getOutput()).contains("myapp", "v1.0");
    }

    // =============================================================================
    // 2. Docker Build 테스트
    // =============================================================================

    @Test
    @DisplayName("2-1. 기본 빌드 (태그 없음)")
    void testBuildWithoutTag() {
        // Given
        ParsedDockerCommand command = ParsedDockerCommand.builder()
                .command("docker build .")
                .subCommand("build")
                .arguments(List.of("."))
                .valid(true)
                .build();

        // When
        CommandExecuteResponse result = imageService.executeCommand(command, simulationId);

        // Then
        assertThat(result.getSuccess()).isTrue();
        assertThat(result.getOutput()).contains("Building image from Dockerfile...");
        verify(imageRepository).save(any(ImageSimulation.class));
    }

    @Test
    @DisplayName("2-2. 태그 지정 빌드")
    void testBuildWithTag() {
        // Given
        ParsedDockerCommand command = ParsedDockerCommand.builder()
                .command("docker build -t myapp:v1.0 .")
                .subCommand("build")
                .arguments(List.of("."))
                .options(java.util.Map.of("-t", "myapp:v1.0"))
                .valid(true)
                .build();

        // When
        CommandExecuteResponse result = imageService.executeCommand(command, simulationId);

        // Then
        assertThat(result.getSuccess()).isTrue();
        assertThat(result.getOutput()).contains("Successfully tagged myapp:v1.0");
        verify(imageRepository).save(any(ImageSimulation.class));
    }

    @Test
    @DisplayName("2-3. 동일 태그 덮어쓰기")
    void testBuildOverwrite() {
        // Given
        ParsedDockerCommand command = ParsedDockerCommand.builder()
                .command("docker build -t myapp:v1.0 .")
                .subCommand("build")
                .arguments(List.of("."))
                .options(java.util.Map.of("-t", "myapp:v1.0"))
                .valid(true)
                .build();

        when(imageRepository.findBySimulationIdAndNameAndTag(simulationId, "myapp", "v1.0"))
                .thenReturn(Optional.of(sampleImage));

        // When
        CommandExecuteResponse result = imageService.executeCommand(command, simulationId);

        // Then
        assertThat(result.getSuccess()).isTrue();
        assertThat(result.getOutput()).contains("덮어씁니다");
        verify(imageRepository).delete(sampleImage); // 기존 이미지 삭제
        verify(imageRepository).save(any(ImageSimulation.class)); // 새 이미지 저장
    }

    // =============================================================================
    // 3. Docker Tag 테스트
    // =============================================================================

    @Test
    @DisplayName("3-1. 이미지 태그 성공")
    void testTagSuccess() {
        // Given
        ParsedDockerCommand command = ParsedDockerCommand.builder()
                .command("docker tag myapp:v1.0 myapp:stable")
                .subCommand("tag")
                .arguments(List.of("myapp:v1.0", "myapp:stable"))
                .valid(true)
                .build();

        when(imageRepository.findBySimulationIdAndNameAndTag(simulationId, "myapp", "v1.0"))
                .thenReturn(Optional.of(sampleImage));
        when(officialImageService.extractImageName("myapp")).thenReturn("myapp");
        when(officialImageService.extractNamespace("myapp")).thenReturn(null);

        // When
        CommandExecuteResponse result = imageService.executeCommand(command, simulationId);

        // Then
        assertThat(result.getSuccess()).isTrue();
        verify(imageRepository).save(any(ImageSimulation.class));
    }

    // =============================================================================
    // 4. Docker Push 테스트
    // =============================================================================

    @Test
    @DisplayName("4-1. 네임스페이스 없는 이미지 푸시 실패")
    void testPushWithoutNamespace() {
        // Given
        ParsedDockerCommand command = ParsedDockerCommand.builder()
                .command("docker push myapp:v1.0")
                .subCommand("push")
                .arguments(List.of("myapp:v1.0"))
                .valid(true)
                .build();

        when(officialImageService.hasNamespace("myapp")).thenReturn(false);

        // When
        CommandExecuteResponse result = imageService.executeCommand(command, simulationId);

        // Then
        assertThat(result.getSuccess()).isFalse();
        assertThat(result.getOutput()).contains("네임스페이스가 없는 이미지는 push할 수 없습니다");
    }

    // =============================================================================
    // 5. 논리적 일관성 테스트
    // =============================================================================

    @Test
    @DisplayName("5-1. 메타데이터 일관성 검증")
    void testMetadataConsistency() {
        // Given
        ParsedDockerCommand command = ParsedDockerCommand.builder()
                .command("docker build -t myregistry/myapp:production .")
                .subCommand("build")
                .arguments(List.of("."))
                .options(java.util.Map.of("-t", "myregistry/myapp:production"))
                .valid(true)
                .build();

        // When
        CommandExecuteResponse result = imageService.executeCommand(command, simulationId);

        // Then
        assertThat(result.getSuccess()).isTrue();
        verify(imageRepository).save(argThat(image -> "myapp".equals(image.getName()) &&
                "production".equals(image.getTag()) &&
                ImageSource.BUILT.equals(image.getSource())));
    }

    @Test
    @DisplayName("5-2. ensureImageExists 메서드 검증")
    void testEnsureImageExists() {
        // Given
        when(imageRepository.findBySimulationIdAndNameAndTag(simulationId, "nginx", "latest"))
                .thenReturn(Optional.empty());

        // When
        imageService.ensureImageExists(simulationId, "nginx", "latest");

        // Then
        verify(imageRepository).save(argThat(image -> "nginx".equals(image.getName()) &&
                "latest".equals(image.getTag()) &&
                ImageSource.PULLED.equals(image.getSource())));
    }

    @Test
    @DisplayName("5-3. getCurrentImages 메서드 검증")
    void testGetCurrentImages() {
        // Given
        List<ImageSimulation> images = Arrays.asList(sampleImage);
        when(imageRepository.findBySimulationId(simulationId)).thenReturn(images);

        // When
        var result = imageService.getCurrentImages(simulationId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("myapp");
        assertThat(result.get(0).getTag()).isEqualTo("v1.0");
    }
}