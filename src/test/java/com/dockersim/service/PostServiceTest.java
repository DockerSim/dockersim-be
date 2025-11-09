package com.dockersim.service;

import com.dockersim.domain.Post;
import com.dockersim.domain.PostLike;
import com.dockersim.domain.enums.PostType;
import com.dockersim.dto.request.PostRequest;
import com.dockersim.dto.response.PostResponse;
import com.dockersim.repository.PostLikeRepository;
import com.dockersim.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    private Post post;
    private String author = "testUser";

    @BeforeEach
    void setUp() {
        post = new Post("Test Title", "Test Content", author, PostType.QUESTION, "#java #spring");
    }

    @Test
    @DisplayName("게시글 작성 성공")
    void createPost_success() {
        // given
        PostRequest request = new PostRequest("New Title", "New Content", PostType.TECHNICAL, "#docker");
        given(postRepository.save(any(Post.class))).willReturn(post);

        // when
        PostResponse response = postService.createPost(request, author);

        // then
        assertThat(response.getTitle()).isEqualTo("Test Title");
        then(postRepository).should().save(any(Post.class));
    }

    @Test
    @DisplayName("게시글 단건 조회 성공")
    void readPost_success() {
        // given
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(postLikeRepository.countByPostId(1L)).willReturn(5L);

        // when
        PostResponse response = postService.readPost(1L);

        // then
        assertThat(response.getTitle()).isEqualTo("Test Title");
        assertThat(response.getLikesCount()).isEqualTo(5);
        assertThat(post.getViews()).isEqualTo(1); // 조회수 증가 확인
        then(postRepository).should().findById(1L);
        then(postLikeRepository).should().countByPostId(1L);
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void updatePost_success() {
        // given
        PostRequest request = new PostRequest("Updated Title", "Updated Content", PostType.SIMULATION, "#k8s");
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        // when
        postService.updatePost(1L, request, author);

        // then
        assertThat(post.getTitle()).isEqualTo("Updated Title");
        assertThat(post.getContent()).isEqualTo("Updated Content");
        then(postRepository).should().findById(1L);
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void deletePost_success() {
        // given
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        willDoNothing().given(postRepository).delete(post);

        // when
        postService.deletePost(1L, author);

        // then
        then(postRepository).should().findById(1L);
        then(postRepository).should().delete(post);
    }

    @Test
    @DisplayName("좋아요 토글 - 좋아요 추가")
    void toggleLike_addLike() {
        // given
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(postLikeRepository.findByAuthorAndPost(author, post)).willReturn(Optional.empty());

        // when
        postService.toggleLike(1L, author);

        // then
        then(postLikeRepository).should().save(any(PostLike.class));
    }

    @Test
    @DisplayName("좋아요 토글 - 좋아요 취소")
    void toggleLike_removeLike() {
        // given
        PostLike postLike = new PostLike(author, post);
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(postLikeRepository.findByAuthorAndPost(author, post)).willReturn(Optional.of(postLike));

        // when
        postService.toggleLike(1L, author);

        // then
        then(postLikeRepository).should().delete(postLike);
    }
    
    @Test
    @DisplayName("좋아요 수 조회")
    void getLikesCount() {
        // given
        given(postLikeRepository.countByPostId(1L)).willReturn(10L);

        // when
        int likesCount = postService.getLikesCount(1L);

        // then
        assertThat(likesCount).isEqualTo(10);
        then(postLikeRepository).should().countByPostId(1L);
    }
}
