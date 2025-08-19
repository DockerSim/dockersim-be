package com.dockersim.service.simulation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.dockersim.domain.Simulation;
import com.dockersim.domain.SimulationShareState;
import com.dockersim.domain.User;
import com.dockersim.dto.request.CollaboratorRequest;
import com.dockersim.dto.request.SimulationRequest;
import com.dockersim.dto.response.SimulationResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.SimulationErrorCode;
import com.dockersim.exception.code.UserErrorCode;
import com.dockersim.repository.SimulationRepository;
import com.dockersim.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("SimulationService 단위 테스트")
class SimulationServiceTest {

    @Mock
    private SimulationRepository simulationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SimulationServiceImpl simulationService;

    private User testOwner;
    private User testCollaborator;
    private Simulation testSimulation;
    private SimulationRequest testRequest;
    private UUID simulationId;
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        simulationId = UUID.randomUUID();

        testOwner = User.builder()
            .userId(ownerId)
            .email("owner@example.com")
            .name("Test Owner")
            .createdAt(LocalDateTime.now())
            .build();

        testCollaborator = User.builder()
            .userId(UUID.randomUUID())
            .email("collaborator@example.com")
            .name("Test Collaborator")
            .createdAt(LocalDateTime.now())
            .build();

        testRequest = new SimulationRequest();
        testRequest.setTitle("Test Simulation");
        testRequest.setShareState(SimulationShareState.PRIVATE.name());

        testSimulation = Simulation.from(testRequest, SimulationShareState.PRIVATE,
            testOwner);
    }

    @Test
    @DisplayName("시뮬레이션 생성 - 성공")
    void createSimulation_Success() {
        // given
        given(userRepository.findByUserId(ownerId)).willReturn(Optional.of(testOwner));
        given(simulationRepository.existsByTitleAndOwnerId(testRequest.getTitle(), ownerId))
            .willReturn(false);
        given(simulationRepository.save(any(Simulation.class))).willReturn(testSimulation);

        // when
        SimulationResponse response = simulationService.createSimulation(ownerId, testRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(testRequest.getTitle());
        assertThat(response.getShareState()).isEqualTo(testRequest.getShareState());
        verify(simulationRepository).save(any(Simulation.class));
    }

    @Test
    @DisplayName("시뮬레이션 생성 - 소유자 없음 실패")
    void createSimulation_OwnerNotFound() {
        // given
        given(userRepository.findByUserId(ownerId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> simulationService.createSimulation(ownerId, testRequest))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.USER_NOT_FOUND);

        verify(simulationRepository, never()).save(any());
    }

    @Test
    @DisplayName("시뮬레이션 생성 - 제목 중복 실패")
    void createSimulation_TitleDuplicate() {
        // given
        given(userRepository.findByUserId(ownerId)).willReturn(Optional.of(testOwner));
        given(simulationRepository.existsByTitleAndOwnerId(testRequest.getTitle(), ownerId))
            .willReturn(true);

        // when & then
        assertThatThrownBy(() -> simulationService.createSimulation(ownerId, testRequest))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode",
                SimulationErrorCode.SIMULATION_TITLE_DUPLICATE);

        verify(simulationRepository, never()).save(any());
    }

    @Test
    @DisplayName("시뮬레이션 조회 - READ 상태일 때 누구나 조회 가능")
    void getSimulation_ReadStateAccessible() {
        // given
        testSimulation = Simulation.from(testRequest, SimulationShareState.READ,
            testOwner);
        UUID randomUserId = UUID.randomUUID();

        given(simulationRepository.findBySimulationId(simulationId))
            .willReturn(Optional.of(testSimulation));

        // when
        SimulationResponse response = simulationService.getSimulation(randomUserId, simulationId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(testRequest.getTitle());
        // userRepository.findByUserId가 호출되지 않아야 함 (권한 검증 없이 조회 가능)
        verify(userRepository, never()).findByUserId(any());
    }

    @Test
    @DisplayName("시뮬레이션 조회 - PRIVATE 상태일 때 사용자 없음 실패")
    void getSimulation_PrivateSimulationUserNotFound() {
        // given
        testSimulation = Simulation.from(testRequest, SimulationShareState.PRIVATE, testOwner);
        UUID nonExistentUserId = UUID.randomUUID();

        given(simulationRepository.findBySimulationId(simulationId))
            .willReturn(Optional.of(testSimulation));
        given(userRepository.findByUserId(nonExistentUserId))
            .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> simulationService.getSimulation(nonExistentUserId, simulationId))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("시뮬레이션 수정 - WRITE에서 다른 상태로 변경 시 협업자 제거")
    void updateSimulation_RemoveCollaboratorsOnWriteStateChange() {
        // given
        testSimulation = Simulation.from(testRequest, SimulationShareState.WRITE,
            testOwner);
        testSimulation.addCollaborator(testCollaborator, testOwner);

        SimulationRequest updateRequest = new SimulationRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setShareState(SimulationShareState.PRIVATE.name());

        given(userRepository.findByUserId(ownerId)).willReturn(Optional.of(testOwner));
        given(simulationRepository.findBySimulationId(simulationId))
            .willReturn(Optional.of(testSimulation));
        given(simulationRepository.save(any(Simulation.class))).willReturn(testSimulation);

        // when
        simulationService.updateSimulation(ownerId, simulationId, updateRequest);

        // then
        assertThat(testSimulation.getCollaborators()).isEmpty();
        assertThat(testSimulation.getShareState()).isEqualTo(SimulationShareState.PRIVATE);
    }

    @Test
    @DisplayName("협업자 초대 - 최대 인원(6명) 초과 시 실패")
    void inviteCollaborator_MaxCollaboratorsReached() {
        // given
        testSimulation = Simulation.from(testRequest, SimulationShareState.WRITE, testOwner);

        given(userRepository.findByUserId(ownerId)).willReturn(Optional.of(testOwner));
        given(simulationRepository.findBySimulationIdWithCollaborators(simulationId))
            .willReturn(Optional.of(testSimulation));
        given(simulationRepository.countCollaborators(simulationId)).willReturn(6L);

        CollaboratorRequest request = new CollaboratorRequest(testCollaborator.getEmail());

        // when & then
        assertThatThrownBy(() ->
            simulationService.inviteCollaborator(simulationId, ownerId, request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode",
                SimulationErrorCode.SIMULATION_MAX_COLLABORATORS_REACHED);

        verify(simulationRepository, never()).save(any());
    }

    @Test
    @DisplayName("협업자 초대 - WRITE 상태가 아닐 때 실패")
    void inviteCollaborator_NotWritableState() {
        // given
        testSimulation = Simulation.from(testRequest, SimulationShareState.PRIVATE,
            testOwner);

        given(userRepository.findByUserId(ownerId)).willReturn(Optional.of(testOwner));
        given(simulationRepository.findBySimulationIdWithCollaborators(simulationId))
            .willReturn(Optional.of(testSimulation));

        CollaboratorRequest request = new CollaboratorRequest(testCollaborator.getEmail());

        // when & then
        assertThatThrownBy(() ->
            simulationService.inviteCollaborator(simulationId, ownerId, request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", SimulationErrorCode.SIMULATION_NOT_WRITABLE);

        verify(simulationRepository, never()).save(any());
    }
}
