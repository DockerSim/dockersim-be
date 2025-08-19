package com.dockersim.context;

import java.util.UUID;

public class SimulationContextHolder {

    private static final ThreadLocal<UUID> SIMULATION_ID = new ThreadLocal<>();

    public static UUID getSimulationId() {
        return SIMULATION_ID.get();
    }

    public static void setSimulationId(UUID simulationId) {
        SIMULATION_ID.set(simulationId);
    }

    public static void clear() {
        SIMULATION_ID.remove();
    }
}
