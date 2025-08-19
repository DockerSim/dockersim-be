package com.dockersim.controller;

import com.dockersim.dto.request.PostCommentRequest;
import com.dockersim.dto.response.PostCommentResponse;
import com.dockersim.service.CommentService;
import com.dockersim.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 새로운 댓글을 작성합니다.
    @PostMapping
    public ResponseEntity<ApiResponse<PostCommentResponse>> createComment(@Valid @RequestBody PostCommentRequest requestDto) {
        // 임시로 'author_name'을 사용합니다. 실제로는 인증 정보를 활용해야 합니다.
        PostCommentResponse response = commentService.createComment(requestDto, "author_name");
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // 특정 게시글의 모든 댓글을 조회합니다.
    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<List<PostCommentResponse>>> getCommentsByPostId(@PathVariable Long postId) {
        List<PostCommentResponse> comments = commentService.readCommentsByPostId(postId);
        return ResponseEntity.ok(ApiResponse.ok(comments));
    }

    // 특정 댓글의 내용을 수정합니다.
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<PostCommentResponse>> updateComment(@PathVariable Long commentId, @Valid @RequestBody PostCommentRequest requestDto) {
        // 임시로 'author_name'을 사용합니다.
        PostCommentResponse updatedComment = commentService.updateComment(commentId, requestDto, "author_name");
        return ResponseEntity.ok(ApiResponse.ok(updatedComment));
    }

    // 특정 댓글을 삭제합니다.
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long commentId) {
        // 임시로 'author_name'을 사용합니다.
        commentService.deleteComment(commentId, "author_name");
        return ResponseEntity.noContent().build();
    }

    // 특정 사용자가 작성한 모든 댓글을 조회합니다.
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<PostCommentResponse>>> getMyComments(@RequestParam String author) {
        List<PostCommentResponse> myComments = commentService.getCommentsByAuthor(author);
        return ResponseEntity.ok(ApiResponse.ok(myComments));
    }
}
