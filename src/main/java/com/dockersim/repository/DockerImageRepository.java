package com.dockersim.repository;

import com.dockersim.domain.DockerImage;
import com.dockersim.domain.ImageLocation;
import com.dockersim.domain.Simulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DockerImageRepository extends JpaRepository<DockerImage, Long> {

    @Modifying
    @Query("DELETE FROM DockerImage di WHERE di.location = :location")
    void deleteByLocation(@Param("location") ImageLocation location);

    Optional<DockerImage> findBySimulationAndNamespaceAndNameAndTagAndLocation(
            Simulation simulation,
            String namespace,
            String name,
            String tag,
            ImageLocation location
    );

    Optional<DockerImage> findBySimulationAndNameAndTagAndLocation(
            Simulation simulation,
            String name,
            String tag,
            ImageLocation location
    );

    List<DockerImage> findBySimulationAndNamespaceAndNameAndLocation(
            Simulation simulation,
            String namespace,
            String name,
            ImageLocation location
    );

    Optional<DockerImage> findBySimulationAndShortHexIdAndLocation(Simulation simulation, String shortHexId,
                                                                   ImageLocation location);

    // -----------------------------------------------------------------

    Optional<DockerImage> findBySimulationAndHexIdStartsWithAndLocation(Simulation simulation, String hexId,
                                                                        ImageLocation location);

    @Query("SELECT d FROM DockerImage d "
            + "WHERE d.simulation = :simulation "
            + "AND d.location = 'LOCAL' "
            + "AND d.name <> '<none>' "
            + "AND d.tag <> '<none>'")
    List<DockerImage> findNotDanglingImageBySimulation(@Param("simulation") Simulation simulation);

    @Query("SELECT d FROM DockerImage d "
            + "WHERE d.simulation = :simulation "
            + "AND d.location = 'LOCAL'")
    List<DockerImage> findBySimulation(@Param("simulation") Simulation simulation);

    @Query("SELECT d FROM DockerImage d "
            + "WHERE d.simulation = :simulation "
            + "AND d.name = '<none>' "
            + "AND d.tag = '<none>' "
            + "AND d.location = 'LOCAL'")
    List<DockerImage> findDanglingImageBySimulationInLocal(@Param("simulation") Simulation simulation);

    @Query("SELECT d FROM DockerImage d "
            + "WHERE d.simulation = :simulation "
            + "AND d.containers IS EMPTY")
    List<DockerImage> findUnreferencedImageBySimulationInLocal(@Param("simulation") Simulation simulation);

    @Query("SELECT d FROM DockerImage  d "
            + "WHERE d.simulation = :simulation "
            + "AND d.namespace = :namespace "
            + "AND d.name = :name "
            + "AND d.location = 'HUB'")
    List<DockerImage> findAllBySimulationAndNamespaceAndNameInHub(@Param("simulation") Simulation simulation,
                                                                  @Param("namespace") String namespace, @Param("name") String name);

    // image pull
    @Query("SELECT d FROM DockerImage  d "
            + "WHERE "
            + "d.name = :name "
            + "AND d.tag = :tag "
            + "AND d.location = 'HUB'")
    Optional<DockerImage> findBySimulationAndNameAndTagInHub(
            @Param("simulation") Simulation simulation,
            @Param("name") String name,
            @Param("tag") String tag
    );

    // ---------------------------------------------------------------------------------------------------------

    Optional<DockerImage> findByNameAndNamespaceAndTagAndLocationAndSimulation(String name,
                                                                               String namespace, String tag, ImageLocation location, Simulation simulation);

    boolean existsByNameAndNamespaceAndTagAndLocationAndSimulation(String name, String namespace,
                                                                   String tag, ImageLocation location, Simulation simulation);

    List<DockerImage> findAllBySimulationAndLocation(Simulation simulation, ImageLocation location);

    /*
    image push -a
     */
    @Query("SELECT d FROM DockerImage  d "
            + "WHERE d.simulation = :simulation "
            + "AND d.namespace = :namespace "
            + "AND d.name = :name "
            + "AND d.location = 'LOCAL'")
    List<DockerImage> findAllBySimulationAndNamespaceAndName(
            @Param("simulation") Simulation simulation,
            @Param("namespace") String namespace,
            @Param("name") String name
    );

    @Query("""
            select 1 from DockerImage i
            	where i.simulation = :simulation
            		and (i.name = :identifier or i.shortHexId = :identifier)
            		and i.location = :location
            """)
    boolean existsByIdentifier(
            @Param("simulation") Simulation simulation,
            @Param("identifier") String identifier,
            @Param("location") ImageLocation location
    );

    @Query("""
            select i from DockerImage i
            	where i.simulation = :simulation
            		and (i.name = :identifier or i.shortHexId = :identifier)
            		and i.location = :location
            """)
    Optional<DockerImage> findByIdentifier(
            @Param("simulation") Simulation simulation,
            @Param("identifier") String identifier,
            @Param("location") ImageLocation location
    );


    @Query(
            """
                    select i from DockerImage i
                    where i.simulation = :simulation
                        and i.name = :name
                        and i.location = 'HUB'
                    """
    )
    List<DockerImage> findAllBySimulationAndNameAndInHub(
            @Param("simulation") Simulation simulation,
            @Param("name") String name
    );

}
