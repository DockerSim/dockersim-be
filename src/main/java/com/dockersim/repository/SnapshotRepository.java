package com.dockersim.repository;

import com.dockersim.entity.Snapshot;
import com.dockersim.entity.User;
import com.dockersim.entity.enums.SnapshotStateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SnapshotRepository extends JpaRepository<Snapshot, Long> {

    List<Snapshot> findBySimulationIdOrderByCreatedAtDesc(String simulationId);

    List<Snapshot> findByOwnerOrderByCreatedAtDesc(User owner);

    List<Snapshot> findBySimulationIdAndStateTypeOrderByCreatedAtDesc(String simulationId, SnapshotStateType stateType);

    Optional<Snapshot> findBySimulationIdAndName(String simulationId, String name);

    @Query("SELECT s FROM Snapshot s WHERE s.simulationId = :simulationId AND s.createdAt >= :since " +
            "ORDER BY s.createdAt DESC")
    List<Snapshot> findRecentSnapshots(@Param("simulationId") String simulationId,
            @Param("since") LocalDateTime since);

    @Query("SELECT s FROM Snapshot s WHERE s.owner = :owner AND " +
            "(s.name LIKE %:searchTerm% OR s.description LIKE %:searchTerm%) " +
            "ORDER BY s.createdAt DESC")
    List<Snapshot> searchByOwnerAndKeyword(@Param("owner") User owner,
            @Param("searchTerm") String searchTerm);

    boolean existsBySimulationIdAndName(String simulationId, String name);

    long countBySimulationIdAndStateType(String simulationId, SnapshotStateType stateType);
}