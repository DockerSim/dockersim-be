package com.dockersim.integration;

import com.dockersim.domain.Post;
import com.dockersim.domain.enums.PostType;
import com.dockersim.dto.request.PostRequest;
import com.dockersim.repository.PostLikeRepository;
import com.dockersim.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    private Post savedPost;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        Post post = new Post("Test Title", "Test Content", "testuser", PostType.QUESTION, "#java");
        savedPost = postRepository.save(post);
    }

    @Test
    @DisplayName("게시글 작성 통합 테스트")
    @WithMockUser(username = "testuser")
    void createPost() throws Exception {
        // given
        PostRequest request = new PostRequest("New Title", "New Content", PostType.TECHNICAL, "#docker");

        // when & then
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("New Title"));
    }

    @Test
    @DisplayName("게시글 목록 조회 통합 테스트")
    @WithMockUser
    void getAllPosts() throws Exception {
        // when & then
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].title").value("Test Title"));
    }

    @Test
    @DisplayName("게시글 단건 조회 통합 테스트")
    @WithMockUser
    void getPost() throws Exception {
        // when & then
        mockMvc.perform(get("/api/posts/{postId}", savedPost.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Test Title"))
                .andExpect(jsonPath("$.data.views").value(1));
    }

    @Test
    @DisplayName("게시글 수정 통합 테스트")
    @WithMockUser(username = "testuser")
    void updatePost() throws Exception {
        // given
        PostRequest request = new PostRequest("Updated Title", "Updated Content", PostType.SIMULATION, "#k8s");

        // when & then
        mockMvc.perform(put("/api/posts/{postId}", savedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Updated Title"));
    }

    @Test
    @DisplayName("게시글 삭제 통합 테스트")
    @WithMockUser(username = "testuser")
    void deletePost() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/posts/{postId}", savedPost.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertThat(postRepository.findById(savedPost.getId())).isEmpty();
    }

    @Test
    @DisplayName("게시글 좋아요 토글 통합 테스트")
    @WithMockUser(username = "likingUser")
    void toggleLike() throws Exception {
        // when & then
        // 1. 좋아요 추가
        mockMvc.perform(post("/api/posts/{postId}/like", savedPost.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertThat(postLikeRepository.countByPostId(savedPost.getId())).isEqualTo(1);

        // 2. 좋아요 취소
        mockMvc.perform(post("/api/posts/{postId}/like", savedPost.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertThat(postLikeRepository.countByPostId(savedPost.getId())).isZero();
    }

    @Test
    @DisplayName("게시글 좋아요 수 조회 통합 테스트")
    @WithMockUser
    void getLikesCount() throws Exception {
        // when & then
        mockMvc.perform(get("/api/posts/{postId}/likes", savedPost.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(0));
    }
}
