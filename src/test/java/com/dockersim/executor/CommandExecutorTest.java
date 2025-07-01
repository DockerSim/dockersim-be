// 이 클래스는 CommandExecutor의 명령어 실행 기능을 테스트합니다.
// 주요 테스트:
// - testExecuteContainerRunCommand : docker run 명령어 실행 테스트
// - testExecuteImagePullCommand : docker pull 명령어 실행 테스트
// - testExecuteContainerStopCommand : docker stop 명령어 실행 테스트
// - testExecuteUnknownCommand : 알 수 없는 명령어 예외 테스트

package com.dockersim.executor;

import com.dockersim.dto.CommandExecuteResult;
import com.dockersim.parser.ParsedDockerCommand;
import com.dockersim.service.ContainerService;
import com.dockersim.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandExecutorTest {

    @Mock
    private ContainerService containerService;

    @Mock
    private ImageService imageService;

    private CommandExecutor commandExecutor;

    @BeforeEach
    void setUp() {
        commandExecutor = new CommandExecutor(containerService, imageService);
    }

    @Test
    @DisplayName("docker run 명령어 실행 테스트")
    void testExecuteContainerRunCommand() {
        // Given
        ParsedDockerCommand command = new ParsedDockerCommand(
                "run",
                List.of("-d"),
                Map.of("-p", List.of("8080:80")),
                List.of("nginx"),
                "CONTAINER");

        CommandExecuteResult expectedResult = new CommandExecuteResult(
                true, "컨테이너 nginx가 성공적으로 실행되었습니다.", "container123");

        when(containerService.executeRunCommand(command)).thenReturn(expectedResult);

        // When
        CommandExecuteResult result = commandExecutor.execute(command);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("컨테이너 nginx가 성공적으로 실행되었습니다.", result.getMessage());
        assertEquals("container123", result.getResourceId());

        verify(containerService, times(1)).executeRunCommand(command);
        verify(imageService, never()).executePullCommand(any());
    }

    @Test
    @DisplayName("docker pull 명령어 실행 테스트")
    void testExecuteImagePullCommand() {
        // Given
        ParsedDockerCommand command = new ParsedDockerCommand(
                "pull",
                List.of(),
                Map.of(),
                List.of("nginx:latest"),
                "IMAGE");

        CommandExecuteResult expectedResult = new CommandExecuteResult(
                true, "이미지 nginx:latest가 성공적으로 다운로드되었습니다.", "image456");

        when(imageService.executePullCommand(command)).thenReturn(expectedResult);

        // When
        CommandExecuteResult result = commandExecutor.execute(command);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("이미지 nginx:latest가 성공적으로 다운로드되었습니다.", result.getMessage());
        assertEquals("image456", result.getResourceId());

        verify(imageService, times(1)).executePullCommand(command);
        verify(containerService, never()).executeRunCommand(any());
    }

    @Test
    @DisplayName("docker stop 명령어 실행 테스트")
    void testExecuteContainerStopCommand() {
        // Given
        ParsedDockerCommand command = new ParsedDockerCommand(
                "stop",
                List.of(),
                Map.of(),
                List.of("container123"),
                "CONTAINER");

        CommandExecuteResult expectedResult = new CommandExecuteResult(
                true, "컨테이너 container123가 성공적으로 중지되었습니다.", "container123");

        when(containerService.executeStopCommand(command)).thenReturn(expectedResult);

        // When
        CommandExecuteResult result = commandExecutor.execute(command);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("컨테이너 container123가 성공적으로 중지되었습니다.", result.getMessage());

        verify(containerService, times(1)).executeStopCommand(command);
    }

    @Test
    @DisplayName("알 수 없는 도메인 명령어 예외 테스트")
    void testExecuteUnknownDomainCommand() {
        // Given
        ParsedDockerCommand command = new ParsedDockerCommand(
                "unknown",
                List.of(),
                Map.of(),
                List.of(),
                "UNKNOWN");

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commandExecutor.execute(command));

        assertEquals("지원되지 않는 도메인입니다: UNKNOWN", exception.getMessage());
    }

    @Test
    @DisplayName("알 수 없는 컨테이너 명령어 예외 테스트")
    void testExecuteUnknownContainerCommand() {
        // Given
        ParsedDockerCommand command = new ParsedDockerCommand(
                "unknown",
                List.of(),
                Map.of(),
                List.of(),
                "CONTAINER");

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commandExecutor.execute(command));

        assertEquals("지원되지 않는 CONTAINER 명령어입니다: unknown", exception.getMessage());
    }

    @Test
    @DisplayName("알 수 없는 이미지 명령어 예외 테스트")
    void testExecuteUnknownImageCommand() {
        // Given
        ParsedDockerCommand command = new ParsedDockerCommand(
                "unknown",
                List.of(),
                Map.of(),
                List.of(),
                "IMAGE");

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commandExecutor.execute(command));

        assertEquals("지원되지 않는 IMAGE 명령어입니다: unknown", exception.getMessage());
    }
}