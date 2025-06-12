package com.dockersim.repository;

import com.dockersim.entity.ImageSimulation;
import com.dockersim.entity.enums.ImageSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageSimulationRepository extends JpaRepository<ImageSimulation, Long> {

        List<ImageSimulation> findBySimulationId(String simulationId);

        Optional<ImageSimulation> findBySimulationIdAndNameAndTag(String simulationId, String name, String tag);

        List<ImageSimulation> findBySimulationIdAndName(String simulationId, String name);

        List<ImageSimulation> findBySimulationIdAndSource(String simulationId, ImageSource source);

        @Query("SELECT i FROM ImageSimulation i WHERE i.simulationId = :simulationId AND " +
                        "(i.name LIKE %:searchTerm% OR i.tag LIKE %:searchTerm%)")
        List<ImageSimulation> searchByNameOrTag(@Param("simulationId") String simulationId,
                        @Param("searchTerm") String searchTerm);

        boolean existsBySimulationIdAndNameAndTag(String simulationId, String name, String tag);
}