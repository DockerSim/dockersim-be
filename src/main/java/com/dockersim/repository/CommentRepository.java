package com.dockersim.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dockersim.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
  
  // 게시글 ID로 댓글 목록 조회
  @Query("SELECT c FROM Comment c WHERE c.board.id = :boardId")
  List<Comment> findByBoardId(@Param("boardId") Long boardId);
  
  // 사용자 ID로 댓글 목록 조회
  @Query("SELECT c FROM Comment c WHERE c.user.id = :userId")
  List<Comment> findByUserId(@Param("userId") Long userId);
  
  // 익명 여부로 댓글 목록 조회
  List<Comment> findByIsAnonymous(Boolean isAnonymous);
  
  // 게시글 ID로 댓글 페이징 조회
  @Query("SELECT c FROM Comment c WHERE c.board.id = :boardId")
  Page<Comment> findByBoardId(@Param("boardId") Long boardId, Pageable pageable);
  
  // 게시글 ID로 댓글 개수 조회
  @Query("SELECT COUNT(c) FROM Comment c WHERE c.board.id = :boardId")
  long countByBoardId(@Param("boardId") Long boardId);
  
  // 사용자가 작성한 특정 게시글의 댓글 조회
  @Query("SELECT c FROM Comment c WHERE c.board.id = :boardId AND c.user.id = :userId")
  List<Comment> findByBoardIdAndUserId(@Param("boardId") Long boardId, @Param("userId") Long userId);
}
