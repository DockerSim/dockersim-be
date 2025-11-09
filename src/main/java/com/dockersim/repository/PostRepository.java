package com.dockersim.repository;

import com.dockersim.domain.Post;
import com.dockersim.domain.User;
import com.dockersim.domain.enums.PostType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // 제목, 내용, 태그 기준으로 검색 (복합 검색)
    List<Post> findByTitleContainingOrContentContainingOrTagsContaining(String titleKeyword, String contentKeyword, String tagsKeyword);

    // 게시글 타입 기준으로 필터링
    List<Post> findByType(PostType type);

    // 내가 작성한 게시글 조회
    List<Post> findByAuthor(User author);
    
    // 최신순으로 게시글 조회
    List<Post> findAllByOrderByCreatedAtDesc();
}
