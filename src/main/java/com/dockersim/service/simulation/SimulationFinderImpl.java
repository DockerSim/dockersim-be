package com.dockersim.service.simulation;

import com.dockersim.domain.Simulation;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.SimulationErrorCode;
import com.dockersim.repository.SimulationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Slf4j 임포트
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j // Slf4j 어노테이션 추가
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
        log.info("SimulationFinderImpl: Attempting to find simulation by publicId: {}", publicId); // 로그 추가
        return repo.findByPublicId(publicId).orElseThrow(
            () -> {
                log.error("SimulationFinderImpl: Simulation not found for publicId: {}", publicId); // 에러 로그 추가
                return new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND, publicId);
            });
    }

    @Override
    public Simulation findSimulationWithCollaborators(String publicId) {
        return repo.findByPublicIdWithCollaborators(publicId).orElseThrow(
            () -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND, publicId));
    }
}
