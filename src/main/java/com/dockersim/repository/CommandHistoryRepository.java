// 이 인터페이스는 CommandHistory 엔티티에 대한 데이터 액세스를 담당합니다.
// 주요 메서드:
// - findBySuccess : 성공/실패별 명령어 이력 조회
// - findByResourceId : 리소스 ID별 명령어 이력 조회
// - findByCommandContaining : 명령어 내용으로 검색

package com.dockersim.repository;

import com.dockersim.domain.CommandHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommandHistoryRepository extends JpaRepository<CommandHistory, Long> {

    List<CommandHistory> findBySuccess(boolean success);

    List<CommandHistory> findByResourceId(String resourceId);

    List<CommandHistory> findByCommandContaining(String keyword);

    @Query("SELECT ch FROM CommandHistory ch WHERE ch.executedAt BETWEEN ?1 AND ?2 ORDER BY ch.executedAt DESC")
    List<CommandHistory> findByExecutedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT ch FROM CommandHistory ch ORDER BY ch.executedAt DESC")
    List<CommandHistory> findAllOrderByExecutedAtDesc();
}