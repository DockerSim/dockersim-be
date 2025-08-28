package com.dockersim.repository;

import com.dockersim.domain.DockerVolume;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DockerVolumeRepository extends JpaRepository<DockerVolume, Long> {

    boolean existsByNameAndSimulationId(String name, Long simulationId);

    Optional<DockerVolume> findByNameAndSimulationId(String name, Long simulationId);

    List<DockerVolume> findBySimulationId(Long simulationId);

    /**
     * 시뮬레이션 내에서 사용하지 않는 '익명' 볼륨을 모두 찾습니다. (containerVolumes 컬렉션이 비어있고, anonymous 플래그가 true인 경우)
     */
    @Query("SELECT v FROM DockerVolume v " +
        "WHERE v.simulation.id = :simulationId " +
        "AND v.anonymous = true " +
        "AND v.containerVolumes IS EMPTY")
    List<DockerVolume> findUnusedAnonymousVolumes(@Param("simulationId") Long simulationId);

    /**
     * 시뮬레이션 내에서 사용하지 않는 '모든' 볼륨(익명 + 명명)을 찾습니다. (containerVolumes 컬렉션이 비어있는 경우)
     */
    @Query("SELECT v FROM DockerVolume v " +
        "WHERE v.simulation.id = :simulationId " +
        "AND v.containerVolumes IS EMPTY")
    List<DockerVolume> findAllUnusedVolumes(@Param("simulationId") Long simulationId);

    @Query("SELECT v FROM DockerVolume v " +
        "WHERE v.simulation.id = :simulationId " +
        "AND v.name = :name " +
        "AND v.containerVolumes IS EMPTY"
    )
    Optional<DockerVolume> findUnusedVolumeByNameAndSimulationId(@Param("name") String name,
        @Param("simulationId") Long simulationId);

}
