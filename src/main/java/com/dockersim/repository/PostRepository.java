package com.dockersim.repository;

import com.dockersim.domain.CommunityPost;
import com.dockersim.domain.enums.PostType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // 제목, 내용, 태그 기준으로 검색
    List<Post> findByTitleContaining(String keyword);
    List<Post> findByContentContaining(String keyword);
    List<Post> findByTagsContaining(String keyword);

    // 게시글 타입 기준으로 필터링
    List<Post> findByType(PostType type);

    // 내가 작성한 게시글 조회
    List<Post> findByAuthor(String author);
    
    // 최신순으로 게시글 조회
    List<Post> findAllByOrderByCreatedAtDesc();
}
