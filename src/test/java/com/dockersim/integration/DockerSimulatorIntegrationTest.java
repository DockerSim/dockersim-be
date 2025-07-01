// 이 클래스는 Docker 시뮬레이터의 전체 통합 테스트입니다.
// 주요 테스트:
// - testCompleteDockerWorkflow : 전체 워크플로우 테스트 (pull -> run -> stop -> rm)
// - testDockerCommandParsing : 명령어 파싱 통합 테스트
// - testCommandHistoryStorage : 명령어 이력 저장 테스트

package com.dockersim.integration;

import com.dockersim.domain.CommandHistory;
import com.dockersim.domain.Container;
import com.dockersim.domain.Image;
import com.dockersim.repository.ContainerRepository;
import com.dockersim.repository.ImageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@SpringJUnitConfig
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class DockerSimulatorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ContainerRepository containerRepository;

    @Autowired
    private CommandHistoryRepository commandHistoryRepository;

    @Test
    @DisplayName("완전한 Docker 워크플로우 통합 테스트")
    void testCompleteDockerWorkflow() throws Exception {
        // 1. 이미지 pull
        CommandRequest pullRequest = new CommandRequest("docker pull nginx:latest");

        mockMvc.perform(post("/api/docker/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pullRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("성공적으로 다운로드")));

        // 이미지가 저장되었는지 확인
        Optional<Image> image = imageRepository.findByNameAndTag("nginx", "latest");
        assertTrue(image.isPresent());
        assertEquals("nginx", image.get().getName());
        assertEquals("latest", image.get().getTag());

        // 2. 컨테이너 실행
        CommandRequest runRequest = new CommandRequest("docker run -d -p 8080:80 --name web nginx:latest");

        String response = mockMvc.perform(post("/api/docker/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(runRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("성공적으로 실행")))
                .andReturn().getResponse().getContentAsString();

        CommandResponse runResponse = objectMapper.readValue(response, CommandResponse.class);
        String containerId = runResponse.getResourceId();

        // 컨테이너가 저장되었는지 확인
        Optional<Container> container = containerRepository.findByContainerId(containerId);
        assertTrue(container.isPresent());
        assertEquals("web", container.get().getName());
        assertTrue(container.get().getPorts().contains("8080:80"));
        assertTrue(container.get().isDetached());

        // 3. 컨테이너 중지
        CommandRequest stopRequest = new CommandRequest("docker stop " + containerId);

        mockMvc.perform(post("/api/docker/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stopRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("중지되었습니다")));

        // 4. 컨테이너 삭제
        CommandRequest rmRequest = new CommandRequest("docker rm " + containerId);

        mockMvc.perform(post("/api/docker/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rmRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("삭제되었습니다")));

        // 컨테이너가 삭제되었는지 확인
        Optional<Container> deletedContainer = containerRepository.findByContainerId(containerId);
        assertFalse(deletedContainer.isPresent());

        // 5. 명령어 이력 확인
        List<CommandHistory> history = commandHistoryRepository.findAllOrderByExecutedAtDesc();
        assertEquals(4, history.size()); // pull, run, stop, rm

        // 모든 명령어가 성공했는지 확인
        for (CommandHistory cmd : history) {
            assertTrue(cmd.isSuccess());
        }
    }

    @Test
    @DisplayName("잘못된 명령어 파싱 오류 테스트")
    void testInvalidCommandParsing() throws Exception {
        CommandRequest invalidRequest = new CommandRequest("invalid command");

        mockMvc.perform(post("/api/docker/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("파싱 오류")));

        // 오류 이력이 저장되었는지 확인
        List<CommandHistory> history = commandHistoryRepository.findBySuccess(false);
        assertEquals(1, history.size());
        assertEquals("invalid command", history.get(0).getCommand());
    }

    @Test
    @DisplayName("명령어 이력 조회 API 테스트")
    void testCommandHistoryAPI() throws Exception {
        // 몇 개의 명령어 실행
        CommandRequest pullRequest = new CommandRequest("docker pull nginx");
        mockMvc.perform(post("/api/docker/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pullRequest)));

        CommandRequest runRequest = new CommandRequest("docker run nginx");
        mockMvc.perform(post("/api/docker/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(runRequest)));

        // 이력 조회
        mockMvc.perform(get("/api/docker/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].command").value("docker run nginx"))
                .andExpect(jsonPath("$[1].command").value("docker pull nginx"));
    }

    @Test
    @DisplayName("헬스 체크 API 테스트")
    void testHealthCheckAPI() throws Exception {
        mockMvc.perform(get("/api/docker/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Docker Simulator is running!"));
    }
}