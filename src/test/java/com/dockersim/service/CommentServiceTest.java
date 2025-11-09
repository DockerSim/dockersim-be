package com.dockersim.service;

import com.dockersim.domain.Comments;
import com.dockersim.domain.Post;
import com.dockersim.dto.request.PostCommentRequest;
import com.dockersim.dto.response.PostCommentResponse;
import com.dockersim.repository.PostCommentRepository;
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
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private PostCommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    private Post post;
    private Comments comment;
    private String author = "testUser";

    @BeforeEach
    void setUp() {
        post = new Post("Test Title", "Test Content", author, null);
        comment = new Comments("Test Comment", author, post);
    }

    @Test
    @DisplayName("댓글 작성 성공")
    void createComment_success() {
        // given
        PostCommentRequest request = new PostCommentRequest("New Comment", 1L);
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(commentRepository.save(any(Comments.class))).willReturn(comment);

        // when
        PostCommentResponse response = commentService.createComment(request, author);

        // then
        assertThat(response.getContent()).isEqualTo("Test Comment");
        assertThat(response.getAuthor()).isEqualTo(author);
        then(postRepository).should().findById(1L);
        then(commentRepository).should().save(any(Comments.class));
    }

    @Test
    @DisplayName("댓글 작성 실패 - 게시글 없음")
    void createComment_postNotFound() {
        // given
        PostCommentRequest request = new PostCommentRequest("New Comment", 1L);
        given(postRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.createComment(request, author))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("게시글을 찾을 수 없습니다.");
        then(postRepository).should().findById(1L);
        then(commentRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("게시글 ID로 댓글 목록 조회")
    void readCommentsByPostId() {
        // given
        given(commentRepository.findByPostIdOrderByCreatedAtDesc(1L)).willReturn(Collections.singletonList(comment));

        // when
        List<PostCommentResponse> responses = commentService.readCommentsByPostId(1L);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getContent()).isEqualTo("Test Comment");
        then(commentRepository).should().findByPostIdOrderByCreatedAtDesc(1L);
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void updateComment_success() {
        // given
        PostCommentRequest request = new PostCommentRequest("Updated Comment", null);
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

        // when
        PostCommentResponse response = commentService.updateComment(1L, request, author);

        // then
        assertThat(response.getContent()).isEqualTo("Updated Comment");
        assertThat(comment.getContent()).isEqualTo("Updated Comment");
        then(commentRepository).should().findById(1L);
    }

    @Test
    @DisplayName("댓글 수정 실패 - 댓글 없음")
    void updateComment_notFound() {
        // given
        PostCommentRequest request = new PostCommentRequest("Updated Comment", null);
        given(commentRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(1L, request, author))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("댓글을 찾을 수 없습니다.");
        then(commentRepository).should().findById(1L);
    }

    @Test
    @DisplayName("댓글 수정 실패 - 권한 없음")
    void updateComment_unauthorized() {
        // given
        PostCommentRequest request = new PostCommentRequest("Updated Comment", null);
        String anotherUser = "anotherUser";
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(1L, request, anotherUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("댓글 수정 권한이 없습니다.");
        then(commentRepository).should().findById(1L);
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteComment_success() {
        // given
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));
        willDoNothing().given(commentRepository).delete(comment);

        // when
        commentService.deleteComment(1L, author);

        // then
        then(commentRepository).should().findById(1L);
        then(commentRepository).should().delete(comment);
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 댓글 없음")
    void deleteComment_notFound() {
        // given
        given(commentRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.deleteComment(1L, author))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("댓글을 찾을 수 없습니다.");
        then(commentRepository).should().findById(1L);
        then(commentRepository).should(never()).delete(any());
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 권한 없음")
    void deleteComment_unauthorized() {
        // given
        String anotherUser = "anotherUser";
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

        // when & then
        assertThatThrownBy(() -> commentService.deleteComment(1L, anotherUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("댓글 삭제 권한이 없습니다.");
        then(commentRepository).should().findById(1L);
        then(commentRepository).should(never()).delete(any());
    }

    @Test
    @DisplayName("작성자별 댓글 목록 조회")
    void getCommentsByAuthor() {
        // given
        given(commentRepository.findByAuthorOrderByCreatedAtDesc(author)).willReturn(Collections.singletonList(comment));

        // when
        List<PostCommentResponse> responses = commentService.getCommentsByAuthor(author);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getAuthor()).isEqualTo(author);
        then(commentRepository).should().findByAuthorOrderByCreatedAtDesc(author);
    }
}
