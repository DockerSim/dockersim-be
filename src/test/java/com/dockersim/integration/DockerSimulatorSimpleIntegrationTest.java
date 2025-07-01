// 이 클래스는 Docker 시뮬레이터의 간단한 통합 테스트입니다.
// 주요 테스트:
// - testDockerParserAndExecutor : 파서와 실행기 통합 테스트
// - testContainerServiceIntegration : 컨테이너 서비스 통합 테스트

package com.dockersim.integration;

import com.dockersim.executor.CommandExecutor;
import com.dockersim.parser.DockerCommandParser;
import com.dockersim.parser.ParsedDockerCommand;
import com.dockersim.domain.Image;
import com.dockersim.repository.ImageRepository;
import com.dockersim.service.ContainerService;
import com.dockersim.service.ImageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class DockerSimulatorSimpleIntegrationTest {

    @Autowired
    private DockerCommandParser parser;

    @Autowired
    private CommandExecutor executor;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageRepository imageRepository;

    @Test
    @DisplayName("Docker 파서와 실행기 통합 테스트")
    void testDockerParserAndExecutor() {
        // Given
        String command = "docker pull nginx:latest";

        // When
        ParsedDockerCommand parsedCommand = parser.parse(command);
        CommandExecuteResult result = executor.execute(parsedCommand);

        // Then
        assertNotNull(parsedCommand);
        assertEquals("pull", parsedCommand.getSubCommand());
        assertEquals("IMAGE", parsedCommand.getDomain());

        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("다운로드"));
    }

    @Test
    @DisplayName("컨테이너 서비스 통합 테스트")
    void testContainerServiceIntegration() {
        // Given - 먼저 이미지 생성
        Image image = new Image("sha256:nginx123", "nginx", "latest", 100L);
        imageRepository.save(image);

        String runCommand = "docker run -d -p 8080:80 nginx:latest";
        ParsedDockerCommand parsedRunCommand = parser.parse(runCommand);

        // When
        CommandExecuteResult runResult = containerService.executeRunCommand(parsedRunCommand);

        // Then
        assertTrue(runResult.isSuccess());
        assertNotNull(runResult.getResourceId());
        assertTrue(runResult.getMessage().contains("성공적으로 실행"));

        // 컨테이너 중지 테스트
        String stopCommand = "docker stop " + runResult.getResourceId();
        ParsedDockerCommand parsedStopCommand = parser.parse(stopCommand);
        CommandExecuteResult stopResult = containerService.executeStopCommand(parsedStopCommand);

        assertTrue(stopResult.isSuccess());
        assertTrue(stopResult.getMessage().contains("중지"));
    }

    @Test
    @DisplayName("이미지 서비스 통합 테스트")
    void testImageServiceIntegration() {
        // Given
        String pullCommand = "docker pull ubuntu:20.04";
        ParsedDockerCommand parsedCommand = parser.parse(pullCommand);

        // When
        CommandExecuteResult result = imageService.executePullCommand(parsedCommand);

        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("다운로드"));

        // 이미지가 저장되었는지 확인
        assertTrue(imageRepository.existsByNameAndTag("ubuntu", "20.04"));
    }

    @Test
    @DisplayName("복잡한 명령어 파싱 테스트")
    void testComplexCommandParsing() {
        // Given
        String complexCommand = "docker run -d -p 8080:80 --name=\"web server\" -e NODE_ENV=prod nginx:1.21";

        // When
        ParsedDockerCommand parsed = parser.parse(complexCommand);

        // Then
        assertEquals("run", parsed.getSubCommand());
        assertEquals("CONTAINER", parsed.getDomain());
        assertTrue(parsed.getFlags().contains("-d"));
        assertEquals("8080:80", parsed.getOptions().get("-p").get(0));
        assertEquals("web server", parsed.getOptions().get("--name").get(0));
        assertEquals("NODE_ENV=prod", parsed.getOptions().get("-e").get(0));
        assertEquals("nginx:1.21", parsed.getPositionalArgs().get(0));
    }
}