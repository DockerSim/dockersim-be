package com.dockersim.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.dockersim.domain.ContainerStatus;
import com.dockersim.domain.DockerImage;
import com.dockersim.domain.ImageLocation;
import com.dockersim.domain.Simulation;
import com.dockersim.domain.User;
import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.dto.response.ImageListResponse;
import com.dockersim.dto.response.ImageRemoveResponse;
import com.dockersim.repository.DockerImageRepository;
import com.dockersim.service.container.ContainerFinder;
import com.dockersim.service.image.DockerImageFinder;
import com.dockersim.service.image.DockerImageServiceImpl;
import com.dockersim.service.simulation.SimulationFinder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DockerImageServiceImplTest {

    @InjectMocks
    private DockerImageServiceImpl dockerImageService;

    @Mock
    private DockerImageRepository dockerImageRepository;
    @Mock
    private DockerImageFinder dockerImageFinder;
    @Mock
    private SimulationFinder simulationFinder;
    @Mock
    private ContainerFinder containerFinder; // 'removeImage' 테스트를 위해 다시 추가

    private Simulation simulation;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().name("testuser").build();
        simulation = Simulation.builder()
            .publicId(UUID.randomUUID().toString())
            .owner(user)
            .dockerImages(new ArrayList<>())
            .dockerContainers(new ArrayList<>())
            .build();
        given(simulationFinder.findByPublicId(any())).willReturn(simulation);
    }

    private DockerImage createTestImage(String id, String name, String tag,
        ImageLocation location) {
        return DockerImage.builder()
            .hexId(id).name(name).namespace(user.getName()).tag(tag)
            .location(location).createdAt(LocalDateTime.now()).simulation(simulation).build();
    }

    private DockerImage createOfficialTestImage(String id, String name, String tag,
        ImageLocation location) {
        return DockerImage.builder()
            .imageId(id).name(name).namespace("library").tag(tag)
            .location(location).createdAt(LocalDateTime.now()).simulation(simulation).build();
    }

    @Test
    @DisplayName("ls: location이 LOCAL인 이미지만 목록에 표시된다")
    void listImages_ShouldOnlyShowLocalImages() {
        DockerImage localImage = createTestImage("id1", "local-img", "1.0",
            ImageLocation.LOCAL);
        given(dockerImageRepository.findAllBySimulationAndLocation(simulation,
            ImageLocation.LOCAL)).willReturn(List.of(localImage));
        ImageListResponse response = dockerImageService.listImages(false, false);
        assertThat(response.getConsole()).anyMatch(line -> line.contains("testuser/local-img"));
    }

    @Test
    @DisplayName("pull: HUB 이미지를 찾아 LOCAL에 복사본을 생성한다")
    void pullImage_ShouldCreateLocalCopyFromHub() {
        String imageNameWithTag = "nginx:latest";
        DockerImage hubImage = createOfficialTestImage("id_nginx", "nginx", "latest",
            ImageLocation.HUB);
        given(dockerImageFinder.parserImageName(anyString())).willReturn(
            Map.of("repository", "nginx", "tag", "latest"));
        given(dockerImageRepository.findByNameAndNamespaceAndTagAndLocationAndSimulation("nginx",
            "library", "latest", ImageLocation.HUB, simulation)).willReturn(Optional.of(hubImage));
        given(dockerImageRepository.existsByNameAndNamespaceAndTagAndLocationAndSimulation("nginx",
            "library", "latest", ImageLocation.LOCAL, simulation)).willReturn(false);
        dockerImageService.pullImage(imageNameWithTag);
        ArgumentCaptor<DockerImage> imageCaptor = ArgumentCaptor.forClass(DockerImage.class);
        verify(dockerImageRepository).save(imageCaptor.capture());
        DockerImage savedImage = imageCaptor.getValue();
        assertThat(savedImage.getName()).isEqualTo("nginx");
        assertThat(savedImage.getNamespace()).isEqualTo("library");
        assertThat(savedImage.getLocation()).isEqualTo(ImageLocation.LOCAL);
    }

    @Test
    @DisplayName("pull: 이미 LOCAL에 존재하면 'up to date'를 반환한다")
    void pullImage_WhenAlreadyExists_ReturnsUpToDate() {
        String imageNameWithTag = "nginx:latest";
        DockerImage hubImage = createOfficialTestImage("id_nginx", "nginx", "latest",
            ImageLocation.HUB);
        given(dockerImageFinder.parserImageName(anyString())).willReturn(
            Map.of("repository", "nginx", "tag", "latest"));
        given(dockerImageRepository.findByNameAndNamespaceAndTagAndLocationAndSimulation("nginx",
            "library", "latest", ImageLocation.HUB, simulation)).willReturn(Optional.of(hubImage));
        given(dockerImageRepository.existsByNameAndNamespaceAndTagAndLocationAndSimulation("nginx",
            "library", "latest", ImageLocation.LOCAL, simulation)).willReturn(true);
        DockerImageResponse response = dockerImageService.pullImage(imageNameWithTag);
        assertThat(response.getConsole()).contains("Image is up to date for " + imageNameWithTag);
        verify(dockerImageRepository, never()).save(any());
    }

    @Test
    @DisplayName("push: LOCAL 이미지를 찾아 HUB에 복사본을 생성한다")
    void pushImage_ShouldCreateHubCopyFromLocal() {
        String imageNameWithTag = "my-app:1.0";
        DockerImage localImage = createTestImage("id_myapp", "my-app", "1.0", ImageLocation.LOCAL);
        given(dockerImageFinder.parserImageName(imageNameWithTag)).willReturn(
            Map.of("repository", "my-app", "tag", "1.0"));
        given(dockerImageRepository.findByNameAndNamespaceAndTagAndLocationAndSimulation("my-app",
            user.getName(), "1.0", ImageLocation.LOCAL, simulation)).willReturn(
            Optional.of(localImage));
        given(dockerImageRepository.existsByNameAndNamespaceAndTagAndLocationAndSimulation("my-app",
            user.getName(), "1.0", ImageLocation.HUB, simulation)).willReturn(false);
        dockerImageService.pushImage(imageNameWithTag);
        ArgumentCaptor<DockerImage> imageCaptor = ArgumentCaptor.forClass(DockerImage.class);
        verify(dockerImageRepository).save(imageCaptor.capture());
        DockerImage savedImage = imageCaptor.getValue();
        assertThat(savedImage.getName()).isEqualTo("my-app");
        assertThat(savedImage.getNamespace()).isEqualTo(user.getName());
        assertThat(savedImage.getLocation()).isEqualTo(ImageLocation.HUB);
    }

    @Test
    @DisplayName("push: 이미 HUB에 존재하면 'already exists'를 반환한다")
    void pushImage_WhenAlreadyExists_ReturnsAlreadyExists() {
        String imageNameWithTag = "my-app:1.0";
        DockerImage localImage = createTestImage("id_myapp", "my-app", "1.0", ImageLocation.LOCAL);
        given(dockerImageFinder.parserImageName(imageNameWithTag)).willReturn(
            Map.of("repository", "my-app", "tag", "1.0"));
        given(dockerImageRepository.findByNameAndNamespaceAndTagAndLocationAndSimulation("my-app",
            user.getName(), "1.0", ImageLocation.LOCAL, simulation)).willReturn(
            Optional.of(localImage));
        given(dockerImageRepository.existsByNameAndNamespaceAndTagAndLocationAndSimulation("my-app",
            user.getName(), "1.0", ImageLocation.HUB, simulation)).willReturn(true);
        DockerImageResponse response = dockerImageService.pushImage(imageNameWithTag);
        assertThat(response.getConsole()).contains("Image already exists in HUB");
        verify(dockerImageRepository, never()).save(any());
    }

    @Test
    @DisplayName("build: 동일 태그 재빌드 시 기존 LOCAL 이미지는 Dangling 상태가 된다")
    void buildImage_WhenTagExists_ShouldMakeOldLocalImageDangling() {
        String imageNameWithTag = "my-app:1.0";
        DockerImage oldImage = createTestImage("id_old", "my-app", "1.0", ImageLocation.LOCAL);
        given(dockerImageFinder.parserImageName(anyString())).willReturn(
            Map.of("repository", "my-app", "tag", "1.0"));
        given(dockerImageRepository.findByNameAndNamespaceAndTagAndLocationAndSimulation("my-app",
            user.getName(), "1.0", ImageLocation.LOCAL, simulation)).willReturn(
            Optional.of(oldImage));
        dockerImageService.buildImage(imageNameWithTag);
        ArgumentCaptor<DockerImage> imageCaptor = ArgumentCaptor.forClass(DockerImage.class);
        verify(dockerImageRepository, times(2)).save(imageCaptor.capture());
        DockerImage capturedImage = imageCaptor.getAllValues().stream()
            .filter(img -> "<none>".equals(img.getName())).findFirst().get();
        assertThat(capturedImage.getName()).isEqualTo("<none>");
        assertThat(capturedImage.getTag()).isEqualTo("<none>");
    }

    @Test
    @DisplayName("prune: 사용하지 않는 LOCAL의 Dangling 이미지만 삭제한다")
    void pruneImages_ShouldRemoveOnlyUnusedLocalDanglingImages() {
        DockerImage localDangling = createTestImage("id1", "<none>", "<none>", ImageLocation.LOCAL);
        DockerImage localUsed = createTestImage("id2", "my-app", "latest", ImageLocation.LOCAL);
        given(dockerImageRepository.findAllBySimulationAndLocation(simulation,
            ImageLocation.LOCAL)).willReturn(List.of(localDangling, localUsed));
        List<String> prunedIds = dockerImageService.pruneImages();
        assertThat(prunedIds).hasSize(1);
        assertThat(prunedIds.get(0)).isEqualTo("id1");
        verify(dockerImageRepository).deleteAll(List.of(localDangling));
    }

    @Test
    @DisplayName("remove: 컨테이너가 사용 중인 이미지는 삭제할 수 없다")
    void removeImage_FailsWhenContainerIsRunning() {
        String imageNameWithTag = "my-app:1.0";
        DockerImage localImage = createTestImage("id_myapp", "my-app", "1.0", ImageLocation.LOCAL);
        given(dockerImageFinder.findImages(imageNameWithTag, simulation)).willReturn(
            List.of(localImage));
        given(containerFinder.existsByImageIdAndStatus(localImage.getImageId(),
            ContainerStatus.RUNNING)).willReturn(true);
        ImageRemoveResponse response = dockerImageService.removeImage(imageNameWithTag);
        assertThat(response.getConsole().get(0)).contains(
            "conflict: unable to remove repository reference");
        verify(dockerImageRepository, never()).delete(any());
    }

    @Test
    @DisplayName("inspect: LOCAL 이미지의 상세 정보를 반환한다")
    void inspectImage_ReturnsCorrectInfoForLocalImage() {
        String imageNameWithTag = "my-app:1.0";
        DockerImage localImage = createTestImage("id_myapp", "my-app", "1.0", ImageLocation.LOCAL);
        given(dockerImageFinder.findImages(imageNameWithTag, simulation)).willReturn(
            List.of(localImage));
        String result = dockerImageService.inspectImage(imageNameWithTag);
        assertThat(result).contains(localImage.getImageId());
        assertThat(result).contains(localImage.getFullNameWithTag());
        assertThat(result).contains(localImage.getLocation().name());
    }
}
