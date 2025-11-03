package com.dockersim.config;


import com.dockersim.domain.Simulation;
import com.dockersim.domain.User;
import lombok.Getter;

@Getter
public class SimulationUserPrincipal {

    private final Long userId;
    private final String userPublicId;
    private final Long simulationId;
    private final String simulationPublicId;

    public SimulationUserPrincipal(User user, Simulation simulation) {
        this.userId = user.getId();
        this.userPublicId = user.getPublicId();
        this.simulationId = simulation.getId();
        this.simulationPublicId = simulation.getPublicId();
    }
}
