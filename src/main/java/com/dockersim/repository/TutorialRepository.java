package com.dockersim.repository;

import com.dockersim.entity.Tutorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorialRepository extends JpaRepository<Tutorial, Long> {

    List<Tutorial> findByIsActiveTrueOrderByOrderIndexAsc();

    List<Tutorial> findByDifficultyAndIsActiveTrueOrderByOrderIndexAsc(Integer difficulty);

    @Query("SELECT t FROM Tutorial t WHERE t.isActive = true AND " +
            "(t.title LIKE %:searchTerm% OR t.description LIKE %:searchTerm%) " +
            "ORDER BY t.orderIndex ASC")
    List<Tutorial> searchByTitleOrDescription(@Param("searchTerm") String searchTerm);

    @Query("SELECT t FROM Tutorial t WHERE t.isActive = true AND t.difficulty BETWEEN :minDifficulty AND :maxDifficulty "
            +
            "ORDER BY t.orderIndex ASC")
    List<Tutorial> findByDifficultyRange(@Param("minDifficulty") Integer minDifficulty,
            @Param("maxDifficulty") Integer maxDifficulty);
}