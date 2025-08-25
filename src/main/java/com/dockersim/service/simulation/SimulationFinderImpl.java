package com.dockersim.service.simulation;

import com.dockersim.domain.Simulation;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.SimulationErrorCode;
import com.dockersim.repository.SimulationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SimulationFinderImpl implements SimulationFinder {

    private final SimulationRepository simulationRepository;

    @Override
    public Simulation findBySimulationId(String simulationId) {
        return simulationRepository.findBySimulationId(simulationId)
            .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND,
                simulationId));
    }

    @Override
    public Simulation findSimulationWithCollaborators(String simulationId) {
        return simulationRepository.findBySimulationIdWithCollaborators(simulationId)
            .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND,
                simulationId));
    }
}
