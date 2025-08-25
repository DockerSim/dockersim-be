package com.dockersim.service.simulation;

import com.dockersim.domain.Simulation;

public interface SimulationFinder {

    Simulation findBySimulationId(String simulationId);

    Simulation findSimulationWithCollaborators(String simulationId);
}
