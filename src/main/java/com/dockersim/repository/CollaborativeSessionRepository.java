package com.dockersim.repository;

import com.dockersim.entity.CollaborativeSession;
import com.dockersim.entity.User;
import com.dockersim.entity.enums.ShareState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CollaborativeSessionRepository extends JpaRepository<CollaborativeSession, Long> {

    Optional<CollaborativeSession> findBySessionId(String sessionId);

    List<CollaborativeSession> findByHostAndIsActiveTrueOrderByCreatedAtDesc(User host);

    List<CollaborativeSession> findByShareStateAndIsActiveTrueOrderByCreatedAtDesc(ShareState shareState);

    @Query("SELECT cs FROM CollaborativeSession cs WHERE cs.isActive = true AND cs.expiredAt > :now " +
            "ORDER BY cs.createdAt DESC")
    List<CollaborativeSession> findActiveAndNotExpired(@Param("now") LocalDateTime now);

    @Query("SELECT cs FROM CollaborativeSession cs WHERE cs.isActive = true AND " +
            "cs.currentParticipants < cs.maxParticipants AND cs.shareState IN :shareStates " +
            "ORDER BY cs.createdAt DESC")
    List<CollaborativeSession> findJoinableSessions(@Param("shareStates") List<ShareState> shareStates);

    @Query("SELECT cs FROM CollaborativeSession cs WHERE cs.host = :host AND " +
            "(cs.title LIKE %:searchTerm% OR cs.description LIKE %:searchTerm%) " +
            "ORDER BY cs.createdAt DESC")
    List<CollaborativeSession> searchByHostAndKeyword(@Param("host") User host,
            @Param("searchTerm") String searchTerm);

    boolean existsBySessionId(String sessionId);

    long countByHostAndIsActiveTrue(User host);
}