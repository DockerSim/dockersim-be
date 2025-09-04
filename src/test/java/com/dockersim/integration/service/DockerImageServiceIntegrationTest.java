package com.dockersim.integration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dockersim.context.SimulationContextHolder;
import com.dockersim.domain.DockerImage;
import com.dockersim.domain.ImageLocation;
import com.dockersim.domain.Simulation;
import com.dockersim.domain.SimulationShareState;
import com.dockersim.domain.User;
import com.dockersim.dto.response.ImageListResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.integration.IntegrationTestSupport;
import com.dockersim.repository.DockerImageRepository;
import com.dockersim.repository.SimulationRepository;
import com.dockersim.repository.UserRepository;
import com.dockersim.service.image.DockerImageService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("DockerImageService 통합 테스트")
class DockerImageServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private DockerImageService dockerImageService;

    @Autowired
    private DockerImageRepository dockerImageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimulationRepository simulationRepository;

    private User testUser;
    private Simulation testSimulation;

    @BeforeEach
    void setUp() {
        dockerImageRepository.deleteAll();
        simulationRepository.deleteAll();
        userRepository.deleteAll();

        // FIX: Add all required fields for entity creation
        testUser = User.builder()
            .userId(UUID.randomUUID())
            .email("test@test.com")
            .name("tester")
            .createdAt(LocalDateTime.now())
            .build();
        userRepository.save(testUser);

        testSimulation = Simulation.builder()
            .simulationId(UUID.randomUUID())
            .title("Test Sim")
            .shareState(SimulationShareState.READ)
            .owner(testUser)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        simulationRepository.save(testSimulation);

        SimulationContextHolder.setSimulationId(testSimulation.getSimulationId());
    }

    @AfterEach
    void tearDown() {
        SimulationContextHolder.clear();
    }

    @Test
    @DisplayName("이미지 생명주기(build -> push -> pull -> ls -> rm) 통합 테스트")
    void imageLifecycleIntegrationTest() {
        // 네임스페이스를 포함한 전체 이미지 이름으로 테스트를 진행하여 명확성을 높입니다.
        String imageName = testUser.getName() + "/my-app:1.0";
        String repositoryName = testUser.getName() + "/my-app";
        dockerImageService.buildImage(imageName);

        List<DockerImage> localImages = dockerImageRepository.findAllBySimulationAndLocation(
            testSimulation, ImageLocation.LOCAL);
        assertThat(localImages).hasSize(1);
        DockerImage builtImage = localImages.get(0);
        assertThat(builtImage.getNamespace()).isEqualTo(testUser.getName());
        assertThat(builtImage.getName()).isEqualTo("my-app");
        assertThat(builtImage.getLocation()).isEqualTo(ImageLocation.LOCAL);

        dockerImageService.pushImage(imageName);

        List<DockerImage> hubImages = dockerImageRepository.findAllBySimulationAndLocation(
            testSimulation, ImageLocation.HUB);
        assertThat(hubImages).hasSize(1);
        DockerImage pushedImage = hubImages.get(0);
        assertThat(pushedImage.getNamespace()).isEqualTo(testUser.getName());
        assertThat(pushedImage.getName()).isEqualTo("my-app");
        assertThat(pushedImage.getLocation()).isEqualTo(ImageLocation.HUB);

        dockerImageService.pullImage(imageName);

        localImages = dockerImageRepository.findAllBySimulationAndLocation(testSimulation,
            ImageLocation.LOCAL);
        assertThat(localImages).hasSize(1);

        ImageListResponse lsResponse = dockerImageService.listImages(false, false);
        assertThat(lsResponse.getConsole()).anyMatch(line -> line.contains(repositoryName));

        dockerImageService.removeImage(imageName);

        localImages = dockerImageRepository.findAllBySimulationAndLocation(testSimulation,
            ImageLocation.LOCAL);
        assertThat(localImages).isEmpty();

        hubImages = dockerImageRepository.findAllBySimulationAndLocation(testSimulation,
            ImageLocation.HUB);
        assertThat(hubImages).hasSize(1);
    }

    @Test
    @DisplayName("Dangling 이미지 생성 및 prune 테스트")
    void danglingImageAndPruneTest() {
        String imageName = "my-app:1.0";
        dockerImageService.buildImage(imageName);

        dockerImageService.buildImage(imageName);

        List<DockerImage> localImages = dockerImageRepository.findAllBySimulationAndLocation(
            testSimulation, ImageLocation.LOCAL);
        assertThat(localImages).hasSize(2);
        long danglingCount = localImages.stream().filter(img -> "<none>".equals(img.getName()))
            .count();
        assertThat(danglingCount).isEqualTo(1);

        List<String> prunedIds = dockerImageService.pruneImages();
        assertThat(prunedIds).hasSize(1);

        localImages = dockerImageRepository.findAllBySimulationAndLocation(testSimulation,
            ImageLocation.LOCAL);
        assertThat(localImages).hasSize(1);
        assertThat(localImages.get(0).getName()).isEqualTo("my-app");
    }

    @Test
    @DisplayName("Pull 실패: HUB에 존재하지 않는 이미지")
    void pullFailsWhenImageNotFoundInHub() {
        String imageName = "non-existent";
        assertThatThrownBy(() -> dockerImageService.pullImage(imageName))
            .isInstanceOf(BusinessException.class);
    }
}
