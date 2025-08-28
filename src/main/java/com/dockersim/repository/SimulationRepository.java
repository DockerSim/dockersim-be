package com.dockersim.repository;

import com.dockersim.domain.Simulation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulationRepository extends JpaRepository<Simulation, Long> {

    Optional<Simulation> findByPublicId(String publicId);

    boolean existsByPublicId(String publicId);

    @Query("SELECT COUNT(c) FROM Simulation s "
        + "JOIN s.collaborators c "
        + "WHERE s.publicId = :simulationId"
    )
    long countCollaborators(@Param("simulationId") String simulationId);

    @Query("SELECT COUNT(s) > 0 FROM Simulation s " +
        "WHERE s.title = :title AND s.owner.publicId = :ownerId AND s.publicId != :excludeId")
    boolean existsByTitleAndOwnerIdAndNotId(@Param("title") String title,
        @Param("ownerId") String ownerId,
        @Param("excludeId") String excludeId);

    @Query("SELECT COUNT(s) > 0 FROM Simulation s " +
        "WHERE s.title = :title AND s.owner.publicId = :ownerId")
    boolean existsByTitleAndOwnerId(@Param("title") String title, @Param("ownerId") String ownerId);

    @Query("SELECT s FROM Simulation s "
        + "LEFT JOIN FETCH s.collaborators "
        + "WHERE s.publicId = :publicId"
    )
    Optional<Simulation> findByPublicIdWithCollaborators(
        @Param("publicId") String publicId);
}
