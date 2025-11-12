package com.dockersim.repository;

import com.dockersim.domain.Post;
import com.dockersim.domain.User;
import com.dockersim.domain.enums.PostType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 제목, 내용, 태그 기준으로 검색 (복합 검색) - author fetch join 추가
    @Query("SELECT DISTINCT p FROM Post p JOIN FETCH p.author WHERE p.title LIKE %:titleKeyword% OR p.content LIKE %:contentKeyword% OR p.tags LIKE %:tagsKeyword%")
    List<Post> findByTitleContainingOrContentContainingOrTagsContaining(@Param("titleKeyword") String titleKeyword, @Param("contentKeyword") String contentKeyword, @Param("tagsKeyword") String tagsKeyword);

    // 게시글 타입 기준으로 필터링 - author fetch join 추가
    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.type = :type")
    List<Post> findByType(@Param("type") PostType type);

    // 내가 작성한 게시글 조회 - author fetch join 추가
    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.author = :author")
    List<Post> findByAuthor(@Param("author") User author);

    // 최신순으로 게시글 조회 - author fetch join 추가
    @Query("SELECT p FROM Post p JOIN FETCH p.author ORDER BY p.createdAt DESC")
    List<Post> findAllByOrderByCreatedAtDesc();
}
