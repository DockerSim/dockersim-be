package com.dockersim.repository;

import com.dockersim.domain.Comments;
import com.dockersim.domain.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostCommentRepository extends JpaRepository<Comments, Long> {

    // 특정 게시글에 속한 댓글을 최신순으로 정렬
    List<Comments> findByPostIdOrderByCreatedAtDesc(Long postId);

    // 특정 사용자가 작성한 댓글을 최신순으로 조회
    List<Comments> findByAuthorOrderByCreatedAtDesc(User author);
    
    // 특정 게시글에 대한 댓글 수 조회
    long countByPostId(Long postId);

    // 특정 사용자가 작성한 댓글 수 조회 d
    long countByAuthor(User author);


}
