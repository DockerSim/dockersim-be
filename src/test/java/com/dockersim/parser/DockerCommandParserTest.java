// 이 클래스는 DockerCommandParser의 명령어 파싱 기능을 테스트합니다.
// 주요 테스트:
// - testParseDockerRunCommand : docker run 명령어 파싱 테스트
// - testParseDockerPullCommand : docker pull 명령어 파싱 테스트
// - testParseComplexDockerRunCommand : 복잡한 옵션이 포함된 run 명령어 테스트
// - testParseDockerStopCommand : docker stop 명령어 파싱 테스트
// - testParseInvalidCommand : 잘못된 명령어 파싱 예외 테스트

package com.dockersim.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

class DockerCommandParserTest {

    private DockerCommandParser parser;

    @BeforeEach
    void setUp() {
        parser = new DockerCommandParser();
    }

    @Test
    @DisplayName("기본 docker run 명령어 파싱 테스트")
    void testParseDockerRunCommand() {
        // Given
        String command = "docker run nginx";

        // When
        ParsedDockerCommand result = parser.parse(command);

        // Then
        assertEquals("run", result.getSubCommand());
        assertEquals(List.of("nginx"), result.getPositionalArgs());
        assertTrue(result.getFlags().isEmpty());
        assertTrue(result.getOptions().isEmpty());
        assertEquals("CONTAINER", result.getDomain());
    }

    @Test
    @DisplayName("플래그가 포함된 docker run 명령어 파싱 테스트")
    void testParseDockerRunWithFlags() {
        // Given
        String command = "docker run -d --rm nginx";

        // When
        ParsedDockerCommand result = parser.parse(command);

        // Then
        assertEquals("run", result.getSubCommand());
        assertEquals(List.of("nginx"), result.getPositionalArgs());
        assertTrue(result.getFlags().contains("-d"));
        assertTrue(result.getFlags().contains("--rm"));
        assertEquals("CONTAINER", result.getDomain());
    }

    @Test
    @DisplayName("키-값 옵션이 포함된 docker run 명령어 파싱 테스트")
    void testParseDockerRunWithOptions() {
        // Given
        String command = "docker run -p 8080:80 --name web nginx";

        // When
        ParsedDockerCommand result = parser.parse(command);

        // Then
        assertEquals("run", result.getSubCommand());
        assertEquals(List.of("nginx"), result.getPositionalArgs());
        assertEquals(List.of("8080:80"), result.getOptions().get("-p"));
        assertEquals(List.of("web"), result.getOptions().get("--name"));
        assertEquals("CONTAINER", result.getDomain());
    }

    @Test
    @DisplayName("복잡한 docker run 명령어 파싱 테스트")
    void testParseComplexDockerRunCommand() {
        // Given
        String command = "docker run -d -p 8080:80 --name=\"web server\" -e NODE_ENV=prod nginx:1.21";

        // When
        ParsedDockerCommand result = parser.parse(command);

        // Then
        assertEquals("run", result.getSubCommand());
        assertEquals(List.of("nginx:1.21"), result.getPositionalArgs());
        assertTrue(result.getFlags().contains("-d"));
        assertEquals(List.of("8080:80"), result.getOptions().get("-p"));
        assertEquals(List.of("web server"), result.getOptions().get("--name"));
        assertEquals(List.of("NODE_ENV=prod"), result.getOptions().get("-e"));
        assertEquals("CONTAINER", result.getDomain());
    }

    @Test
    @DisplayName("반복 옵션이 포함된 docker run 명령어 파싱 테스트")
    void testParseDockerRunWithRepeatedOptions() {
        // Given
        String command = "docker run -e VAR1=value1 -e VAR2=value2 nginx";

        // When
        ParsedDockerCommand result = parser.parse(command);

        // Then
        assertEquals("run", result.getSubCommand());
        assertEquals(List.of("nginx"), result.getPositionalArgs());
        assertEquals(List.of("VAR1=value1", "VAR2=value2"), result.getOptions().get("-e"));
        assertEquals("CONTAINER", result.getDomain());
    }

    @Test
    @DisplayName("docker pull 명령어 파싱 테스트")
    void testParseDockerPullCommand() {
        // Given
        String command = "docker pull nginx:latest";

        // When
        ParsedDockerCommand result = parser.parse(command);

        // Then
        assertEquals("pull", result.getSubCommand());
        assertEquals(List.of("nginx:latest"), result.getPositionalArgs());
        assertEquals("IMAGE", result.getDomain());
    }

    @Test
    @DisplayName("docker stop 명령어 파싱 테스트")
    void testParseDockerStopCommand() {
        // Given
        String command = "docker stop container1 container2";

        // When
        ParsedDockerCommand result = parser.parse(command);

        // Then
        assertEquals("stop", result.getSubCommand());
        assertEquals(List.of("container1", "container2"), result.getPositionalArgs());
        assertEquals("CONTAINER", result.getDomain());
    }

    @Test
    @DisplayName("docker 키워드가 없는 잘못된 명령어 파싱 예외 테스트")
    void testParseInvalidCommandWithoutDocker() {
        // Given
        String command = "run nginx";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> parser.parse(command));
    }

    @Test
    @DisplayName("빈 명령어 파싱 예외 테스트")
    void testParseEmptyCommand() {
        // Given
        String command = "";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> parser.parse(command));
    }

    @Test
    @DisplayName("서브 명령어가 없는 파싱 예외 테스트")
    void testParseCommandWithoutSubCommand() {
        // Given
        String command = "docker";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> parser.parse(command));
    }
}