package com.dockersim.integration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dockersim.domain.SimulationShareState;
import com.dockersim.dto.request.CollaboratorRequest;
import com.dockersim.dto.request.SimulationRequest;
import com.dockersim.dto.request.UserRequest;
import com.dockersim.dto.response.CollaboratorResponse;
import com.dockersim.dto.response.SimulationResponse;
import com.dockersim.dto.response.UserResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.SimulationErrorCode;
import com.dockersim.repository.SimulationRepository;
import com.dockersim.repository.UserRepository;
import com.dockersim.service.simulation.SimulationService;
import com.dockersim.service.user.UserService;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayName("SimulationService 통합 테스트")
class SimulationServiceIntegrationTest {

    @Autowired
    private SimulationService simulationService;

    @Autowired
    private UserService userService;

    @Autowired
    private SimulationRepository simulationRepository;

    @Autowired
    private UserRepository userRepository;

    private UserResponse testOwner;
    private UserResponse testCollaborator;
    private SimulationRequest testRequest;

    @BeforeEach
    void setUp() {
        simulationRepository.deleteAll();
        userRepository.deleteAll();

        // 테스트 사용자 생성
        UserRequest ownerRequest = new UserRequest();
        ownerRequest.setEmail("owner@example.com");
        ownerRequest.setName("Test Owner");
        testOwner = userService.createUser(ownerRequest);

        UserRequest collaboratorRequest = new UserRequest();
        collaboratorRequest.setEmail("collaborator@example.com");
        collaboratorRequest.setName("Test Collaborator");
        testCollaborator = userService.createUser(collaboratorRequest);

        // 테스트용 시뮬레이션 요청
        testRequest = new SimulationRequest();
        testRequest.setTitle("Test Simulation");
        testRequest.setShareState(SimulationShareState.PRIVATE.name());
    }

    @Test
    @DisplayName("시뮬레이션 생성 및 조회 통합 테스트")
    void createAndGetSimulation() {
        // when - 시뮬레이션 생성
        SimulationResponse createResponse = simulationService.createSimulation(
            testOwner.getUserId(), testRequest);

        // then - 생성 결과 검증
        assertThat(createResponse).isNotNull();
        assertThat(createResponse.getTitle()).isEqualTo(testRequest.getTitle());
        assertThat(createResponse.getShareState()).isEqualTo(testRequest.getShareState());

        // when - 생성된 시뮬레이션 조회
        SimulationResponse getResponse = simulationService.getSimulation(
            testOwner.getUserId(), createResponse.getSimulationId());

        // then - 조회 결과 검증
        assertThat(getResponse)
            .usingRecursiveComparison()
            .isEqualTo(createResponse);
    }

    @Test
    @DisplayName("READ 상태 시뮬레이션 누구나 조회 가능")
    void readStateSimulationAccessibleByAll() {
        // given
        testRequest.setShareState(SimulationShareState.READ.name());
        SimulationResponse simulation = simulationService.createSimulation(
            testOwner.getUserId(), testRequest);

        // when - 임의의 사용자로 조회
        UserRequest randomUserRequest = new UserRequest();
        randomUserRequest.setEmail("random@example.com");
        randomUserRequest.setName("Random User");
        UserResponse randomUser = userService.createUser(randomUserRequest);

        SimulationResponse response = simulationService.getSimulation(
            randomUser.getUserId(), simulation.getSimulationId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getSimulationId()).isEqualTo(simulation.getSimulationId());
    }

    @Test
    @DisplayName("WRITE에서 다른 상태로 변경시 협업자 제거")
    void removeCollaboratorsOnStateChange() {
        // given
        testRequest.setShareState(SimulationShareState.WRITE.name());
        SimulationResponse simulation = simulationService.createSimulation(
            testOwner.getUserId(), testRequest);

        // 협업자 추가
        CollaboratorRequest collaboratorRequest = new CollaboratorRequest(
            testCollaborator.getEmail());
        simulationService.inviteCollaborator(
            simulation.getSimulationId(), testOwner.getUserId(), collaboratorRequest);

        // 협업자가 추가되었는지 확인
        List<CollaboratorResponse> collaborators = simulationService.getCollaborators(
            testOwner.getUserId(), simulation.getSimulationId());
        assertThat(collaborators).hasSize(1);

        // when - PRIVATE으로 상태 변경
        SimulationRequest updateRequest = new SimulationRequest();
        updateRequest.setTitle(simulation.getTitle());
        updateRequest.setShareState(SimulationShareState.PRIVATE.name());

        simulationService.updateSimulation(
            testOwner.getUserId(), simulation.getSimulationId(), updateRequest);

        // then - 협업자가 제거되었는지 확인
        List<CollaboratorResponse> afterUpdate = simulationService.getCollaborators(
            testOwner.getUserId(), simulation.getSimulationId());
        assertThat(afterUpdate).isEmpty();
    }

    @Test
    @DisplayName("협업자 6명 초과 초대 실패")
    void maxCollaboratorsLimitTest() {
        // given
        testRequest.setShareState(SimulationShareState.WRITE.name());
        SimulationResponse simulation = simulationService.createSimulation(
            testOwner.getUserId(), testRequest);

        // 6명의 협업자 생성 및 초대
        IntStream.range(0, 6).forEach(i -> {
            UserRequest request = new UserRequest();
            request.setEmail("collaborator" + i + "@example.com");
            request.setName("Collaborator " + i);
            UserResponse collaborator = userService.createUser(request);

            CollaboratorRequest collaboratorRequest = new CollaboratorRequest(
                collaborator.getEmail());
            simulationService.inviteCollaborator(
                simulation.getSimulationId(), testOwner.getUserId(), collaboratorRequest);
        });

        // when & then - 7번째 협업자 초대 시도
        UserRequest extraRequest = new UserRequest();
        extraRequest.setEmail("extra@example.com");
        extraRequest.setName("Extra Collaborator");
        UserResponse extraUser = userService.createUser(extraRequest);

        CollaboratorRequest extraCollaboratorRequest = new CollaboratorRequest(
            extraUser.getEmail());

        assertThatThrownBy(() ->
            simulationService.inviteCollaborator(
                simulation.getSimulationId(), testOwner.getUserId(), extraCollaboratorRequest))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode",
                SimulationErrorCode.SIMULATION_MAX_COLLABORATORS_REACHED);

        // 협업자 수가 여전히 6명인지 확인
        List<CollaboratorResponse> collaborators = simulationService.getCollaborators(
            testOwner.getUserId(), simulation.getSimulationId());
        assertThat(collaborators).hasSize(6);
    }

    @Test
    @DisplayName("WRITE 상태가 아닐 때 협업자 초대 실패")
    void inviteCollaboratorFailsWhenNotWritable() {
        // given
        testRequest.setShareState(SimulationShareState.PRIVATE.name());
        SimulationResponse simulation = simulationService.createSimulation(
            testOwner.getUserId(), testRequest);

        CollaboratorRequest request = new CollaboratorRequest(testCollaborator.getEmail());

        // when & then
        assertThatThrownBy(() ->
            simulationService.inviteCollaborator(
                simulation.getSimulationId(), testOwner.getUserId(), request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", SimulationErrorCode.SIMULATION_NOT_WRITABLE);
    }

    @Test
    @DisplayName("시뮬레이션 제목 중복 체크")
    void duplicateSimulationTitle() {
        // given
        simulationService.createSimulation(testOwner.getUserId(), testRequest);

        // when & then
        assertThatThrownBy(() ->
            simulationService.createSimulation(testOwner.getUserId(), testRequest))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode",
                SimulationErrorCode.SIMULATION_TITLE_DUPLICATE);
    }
}
