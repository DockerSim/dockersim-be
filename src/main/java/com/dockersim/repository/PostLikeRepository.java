package com.dockersim.repository;

import com.dockersim.domain.Post;
import com.dockersim.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 특정 게시글에 대한 좋아요 조회(공감은 한 번만 누를 수 있도록)
    Optional<PostLike> findByAuthorAndPost(String author, Post post);

    // 특정 게시글 좋아요 수 조회
    long countByPostId(Long postId);
    
   // 내가 누른 좋아요 목록 조회
    List<PostLike> findByAuthor(String author);
}
