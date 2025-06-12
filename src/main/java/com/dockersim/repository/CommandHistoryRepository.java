package com.dockersim.repository;

import com.dockersim.entity.CommandHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandHistoryRepository extends JpaRepository<CommandHistory, Long> {

    List<CommandHistory> findByUserIdOrderByExecutedAtDesc(Long userId);

    List<CommandHistory> findByUserIdAndSimulationIdOrderByExecutedAtDesc(Long userId, String simulationId);
}