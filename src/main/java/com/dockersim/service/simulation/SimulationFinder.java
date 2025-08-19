package com.dockersim.service.simulation;

import com.dockersim.domain.Simulation;
import java.util.UUID;

public interface SimulationFinder {

    Simulation findSimulationByUUID(UUID simulationId);

    Simulation findSimulationWithCollaborators(UUID simulationId);
}
