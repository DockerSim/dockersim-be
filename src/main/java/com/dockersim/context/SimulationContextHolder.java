package com.dockersim.context;

public class SimulationContextHolder {

    private static final ThreadLocal<String> SIMULATION_ID = new ThreadLocal<>();

    public static String getSimulationId() {
        return SIMULATION_ID.get();
    }

    public static void setSimulationId(String simulationId) {
        SIMULATION_ID.set(simulationId);
    }

    public static void clear() {
        SIMULATION_ID.remove();
    }
}
