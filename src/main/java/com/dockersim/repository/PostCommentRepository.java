package com.dockersim.repository;

import com.dockersim.domain.CommunityComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostCommentRepository extends JpaRepository<Comment, Long> {

    // 특정 게시글에 속한 댓글을 최신순으로 정렬
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);

    // 특정 사용자가 작성한 댓글을 최신순으로 조회
    List<Comment> findByAuthorOrderByCreatedAtDesc(String author);
    
    // 특정 게시글에 대한 댓글 수 조회
    long countByPostId(Long postId);

    // 특정 사용자가 작성한 댓글 수 조회
    long countByAuthor(String author);


}
