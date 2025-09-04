package com.dockersim.repository;

import com.dockersim.domain.Simulation;
import com.dockersim.domain.SimulationCollaborator;
import com.dockersim.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulationCollaboratorRepository extends
    JpaRepository<SimulationCollaborator, Long> {

    boolean existsBySimulationAndUser(Simulation simulation, User user);
}
