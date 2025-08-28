package com.dockersim.service.simulation;

import com.dockersim.domain.Simulation;

public interface SimulationFinder {

    boolean existsByPublicId(String publicId);

    Simulation findById(Long id);

    Simulation findByPublicId(String publicId);

    Simulation findSimulationWithCollaborators(String simulationId);
}
