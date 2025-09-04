package com.dockersim.service.simulation;

import com.dockersim.domain.Simulation;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.SimulationErrorCode;
import com.dockersim.repository.SimulationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SimulationFinderImpl implements SimulationFinder {

    private final SimulationRepository repo;

    @Override
    public boolean existsByPublicId(String publicId) {
        return repo.existsByPublicId(publicId);
    }

    @Override
    public Simulation findById(Long id) {
        return repo.findById(id).orElseThrow(
            () -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND, id));
    }

    @Override
    public Simulation findByPublicId(String publicId) {
        return repo.findByPublicId(publicId).orElseThrow(
            () -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND, publicId));
    }

    @Override
    public Simulation findSimulationWithCollaborators(String publicId) {
        return repo.findByPublicIdWithCollaborators(publicId).orElseThrow(
            () -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND, publicId));
    }
}
