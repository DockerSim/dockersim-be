package com.dockersim.integration;

import com.dockersim.domain.Comments;
import com.dockersim.domain.Post;
import com.dockersim.domain.User; // Import User
import com.dockersim.dto.request.PostCommentRequest;
import com.dockersim.repository.PostCommentRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostCommentRepository commentRepository;

    private Post savedPost;
    private User testUser; // Add testUser field
    private User user1; // Add user1 field
    private User user2; // Add user2 field

    @BeforeEach
    void setUp() {
        // Create User objects for testing
        testUser = User.builder().publicId("testuser_public_id").name("testuser").email("test@example.com").build();
        user1 = User.builder().publicId("user1_public_id").name("user1").email("user1@example.com").build();
        user2 = User.builder().publicId("user2_public_id").name("user2").email("user2@example.com").build();

        // 테스트를 위한 게시글 미리 저장
        Post post = new Post("Test Title", "Test Content", testUser, null, null); // Pass testUser
        savedPost = postRepository.save(post);
    }

    @Test
    @DisplayName("댓글 작성 통합 테스트")
    @WithMockUser(username = "testuser")
    void createComment() throws Exception {
        // given
        PostCommentRequest request = new PostCommentRequest("New test comment", savedPost.getId());

        // when & then
        mockMvc.perform(post("/api/posts/{postId}/comments", savedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").value("New test comment"))
                .andExpect(jsonPath("$.data.author.name").value("testuser")); // Adjust assertion
    }

    @Test
    @DisplayName("댓글 목록 조회 통합 테스트")
    @WithMockUser
    void getCommentsByPostId() throws Exception {
        // given
        commentRepository.save(new Comments("First comment", user1, savedPost)); // Pass user1
        commentRepository.save(new Comments("Second comment", user2, savedPost)); // Pass user2

        // when & then
        mockMvc.perform(get("/api/posts/{postId}/comments", savedPost.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].content").value("First comment"));
    }

    @Test
    @DisplayName("댓글 수정 통합 테스트")
    @WithMockUser(username = "testuser")
    void updateComment() throws Exception {
        // given
        Comments savedComment = commentRepository.save(new Comments("Original comment", testUser, savedPost)); // Pass testUser
        PostCommentRequest request = new PostCommentRequest("Updated comment", null);

        // when & then
        mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", savedPost.getId(), savedComment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").value("Updated comment"))
                .andExpect(jsonPath("$.data.author.name").value("testuser")); // Adjust assertion
    }

    @Test
    @DisplayName("댓글 삭제 통합 테스트")
    @WithMockUser(username = "testuser")
    void deleteComment() throws Exception {
        // given
        Comments savedComment = commentRepository.save(new Comments("To be deleted", testUser, savedPost)); // Pass testUser

        // when & then
        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", savedPost.getId(), savedComment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
