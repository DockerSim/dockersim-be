// 이 클래스는 ContainerService 구현체의 기능을 테스트합니다.
// 주요 테스트:
// - testExecuteRunCommand : docker run 명령어 실행 테스트
// - testExecuteStopCommand : docker stop 명령어 실행 테스트
// - testExecuteStartCommand : docker start 명령어 실행 테스트
// - testExecuteRemoveCommand : docker rm 명령어 실행 테스트

package com.dockersim.service;

import com.dockersim.domain.Container;
import com.dockersim.domain.ContainerStatus;
import com.dockersim.domain.Image;
import com.dockersim.parser.ParsedDockerCommand;
import com.dockersim.repository.ContainerRepository;
import com.dockersim.repository.ImageRepository;
import com.dockersim.dto.CommandExecuteResult;
import com.dockersim.service.impl.ContainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContainerServiceTest {

    @Mock
    private ContainerRepository containerRepository;

    @Mock
    private ImageRepository imageRepository;

    private ContainerService containerService;

    @BeforeEach
    void setUp() {
        containerService = new ContainerServiceImpl(containerRepository, imageRepository);
    }

    @Test
    @DisplayName("기본 docker run 명령어 실행 테스트")
    void testExecuteRunCommand() {
        // Given
        ParsedDockerCommand command = new ParsedDockerCommand(
                "run",
                List.of(),
                Map.of(),
                List.of("nginx"),
                "CONTAINER");

        Image image = new Image("sha256:nginx123", "nginx", "latest", 100L);
        when(imageRepository.findByNameAndTag("nginx", "latest")).thenReturn(Optional.of(image));
        when(containerRepository.save(any(Container.class))).thenAnswer(invocation -> {
            Container container = invocation.getArgument(0);
            return container;
        });

        // When
        CommandExecuteResult result = containerService.executeRunCommand(command);

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getResourceId());
        assertTrue(result.getMessage().contains("컨테이너가 성공적으로 실행되었습니다"));

        verify(imageRepository, times(1)).findByNameAndTag("nginx", "latest");
        verify(containerRepository, times(1)).save(any(Container.class));
    }

    @Test
    @DisplayName("포트 매핑이 포함된 docker run 명령어 실행 테스트")
    void testExecuteRunCommandWithPortMapping() {
        // Given
        ParsedDockerCommand command = new ParsedDockerCommand(
                "run",
                List.of("-d"),
                Map.of("-p", List.of("8080:80")),
                List.of("nginx"),
                "CONTAINER");

        Image image = new Image("sha256:nginx123", "nginx", "latest", 100L);
        when(imageRepository.findByNameAndTag("nginx", "latest")).thenReturn(Optional.of(image));
        when(containerRepository.save(any(Container.class))).thenAnswer(invocation -> {
            Container container = invocation.getArgument(0);
            return container;
        });

        // When
        CommandExecuteResult result = containerService.executeRunCommand(command);

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getResourceId());

        verify(containerRepository).save(argThat(container -> {
            return container.getPorts().contains("8080:80") &&
                    container.isDetached();
        }));
    }

    @Test
    @DisplayName("이미지가 존재하지 않는 경우 docker run 실패 테스트")
    void testExecuteRunCommandWithNonExistentImage() {
        // Given
        ParsedDockerCommand command = new ParsedDockerCommand(
                "run",
                List.of(),
                Map.of(),
                List.of("nonexistent:latest"),
                "CONTAINER");

        when(imageRepository.findByNameAndTag("nonexistent", "latest")).thenReturn(Optional.empty());

        // When
        CommandExecuteResult result = containerService.executeRunCommand(command);

        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("이미지를 찾을 수 없습니다"));

        verify(containerRepository, never()).save(any(Container.class));
    }

    @Test
    @DisplayName("docker stop 명령어 실행 테스트")
    void testExecuteStopCommand() {
        // Given
        ParsedDockerCommand command = new ParsedDockerCommand(
                "stop",
                List.of(),
                Map.of(),
                List.of("container123"),
                "CONTAINER");

        Container container = new Container("container123", "test-container", "nginx", ContainerStatus.RUNNING);
        when(containerRepository.findByContainerId("container123")).thenReturn(Optional.of(container));
        when(containerRepository.save(any(Container.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CommandExecuteResult result = containerService.executeStopCommand(command);

        // Then
        assertTrue(result.isSuccess());
        assertEquals("container123", result.getResourceId());
        assertTrue(result.getMessage().contains("중지되었습니다"));

        verify(containerRepository, times(1)).save(argThat(c -> c.getStatus() == ContainerStatus.STOPPED));
    }

    @Test
    @DisplayName("존재하지 않는 컨테이너 stop 실패 테스트")
    void testExecuteStopCommandWithNonExistentContainer() {
        // Given
        ParsedDockerCommand command = new ParsedDockerCommand(
                "stop",
                List.of(),
                Map.of(),
                List.of("nonexistent"),
                "CONTAINER");

        when(containerRepository.findByContainerId("nonexistent")).thenReturn(Optional.empty());

        // When
        CommandExecuteResult result = containerService.executeStopCommand(command);

        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("컨테이너를 찾을 수 없습니다"));
    }

    @Test
    @DisplayName("docker start 명령어 실행 테스트")
    void testExecuteStartCommand() {
        // Given
        ParsedDockerCommand command = new ParsedDockerCommand(
                "start",
                List.of(),
                Map.of(),
                List.of("container123"),
                "CONTAINER");

        Container container = new Container("container123", "test-container", "nginx", ContainerStatus.STOPPED);
        when(containerRepository.findByContainerId("container123")).thenReturn(Optional.of(container));
        when(containerRepository.save(any(Container.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CommandExecuteResult result = containerService.executeStartCommand(command);

        // Then
        assertTrue(result.isSuccess());
        assertEquals("container123", result.getResourceId());
        assertTrue(result.getMessage().contains("시작되었습니다"));

        verify(containerRepository, times(1)).save(argThat(c -> c.getStatus() == ContainerStatus.RUNNING));
    }

    @Test
    @DisplayName("docker rm 명령어 실행 테스트")
    void testExecuteRemoveCommand() {
        // Given
        ParsedDockerCommand command = new ParsedDockerCommand(
                "rm",
                List.of(),
                Map.of(),
                List.of("container123"),
                "CONTAINER");

        Container container = new Container("container123", "test-container", "nginx", ContainerStatus.STOPPED);
        when(containerRepository.findByContainerId("container123")).thenReturn(Optional.of(container));

        // When
        CommandExecuteResult result = containerService.executeRemoveCommand(command);

        // Then
        assertTrue(result.isSuccess());
        assertEquals("container123", result.getResourceId());
        assertTrue(result.getMessage().contains("삭제되었습니다"));

        verify(containerRepository, times(1)).delete(container);
    }

    @Test
    @DisplayName("실행 중인 컨테이너 삭제 실패 테스트")
    void testExecuteRemoveRunningContainerFails() {
        // Given
        ParsedDockerCommand command = new ParsedDockerCommand(
                "rm",
                List.of(),
                Map.of(),
                List.of("container123"),
                "CONTAINER");

        Container container = new Container("container123", "test-container", "nginx", ContainerStatus.RUNNING);
        when(containerRepository.findByContainerId("container123")).thenReturn(Optional.of(container));

        // When
        CommandExecuteResult result = containerService.executeRemoveCommand(command);

        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("실행 중인 컨테이너는 삭제할 수 없습니다"));

        verify(containerRepository, never()).delete(any(Container.class));
    }
}