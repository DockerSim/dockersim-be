package com.dockersim.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dockersim.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    
    // 시뮬레이션 ID로 게시글 목록 조회
    List<Board> findBySimulationId(String simulationId);
    
    // 태그를 포함하는 게시글 검색
    @Query("SELECT b FROM Board b WHERE b.tags LIKE %:tag%")
    List<Board> findByTagContaining(@Param("tag") String tag);
    
    // 제목이나 내용에 키워드를 포함하는 게시글 검색
    @Query("SELECT b FROM Board b WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword%")
    List<Board> findByTitleOrContentContaining(@Param("keyword") String keyword);
    
    // 특정 사용자가 작성한 게시글 목록 조회
    List<Board> findByUserId(Long userId);
    
    // 게시글 타입별 조회
    List<Board> findByType(String type);
    
    // 페이징 처리된 게시글 목록 조회
    Page<Board> findAll(Pageable pageable);
}
