package com.dockersim.unit.service;

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
import com.dockersim.service.simulation.SimulationFinder;
import com.dockersim.service.simulation.SimulationServiceImpl;
import com.dockersim.service.user.UserFinder;
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

    @Mock // FIX: Add missing mock
    private UserFinder userFinder;

    @Mock // FIX: Add missing mock
    private SimulationFinder simulationFinder;

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
        given(userFinder.findUserByUUID(ownerId)).willReturn(testOwner);
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
        given(userFinder.findUserByUUID(ownerId)).willThrow(new BusinessException(UserErrorCode.USER_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> simulationService.createSimulation(ownerId, testRequest))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.USER_NOT_FOUND);

        verify(simulationRepository, never()).save(any());
    }

    // ... other tests ...
}
