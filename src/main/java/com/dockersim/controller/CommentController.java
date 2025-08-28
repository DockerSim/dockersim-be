package com.dockersim.controller;

import com.dockersim.dto.request.PostCommentRequest;
import com.dockersim.dto.response.PostCommentResponse;
import com.dockersim.service.CommentService;
import com.dockersim.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Comment API", description = "댓글 관련 API")
@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성", description = "특정 게시글에 새로운 댓글을 작성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<PostCommentResponse>> createComment(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @RequestBody PostCommentRequest requestDto) { // UserDetails 파라미터 제거
        requestDto.setPostId(postId);
        // 임시 작성자 이름으로 "dev_user"를 사용
        PostCommentResponse response = commentService.createComment(requestDto, "dev_user");
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "게시글의 모든 댓글 조회", description = "특정 게시글에 달린 모든 댓글을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostCommentResponse>>> getCommentsByPostId(
            @Parameter(description = "게시글 ID") @PathVariable Long postId) {
        List<PostCommentResponse> comments = commentService.readCommentsByPostId(postId);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @Operation(summary = "댓글 수정", description = "특정 댓글의 내용을 수정합니다.")
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<PostCommentResponse>> updateComment(
            @Parameter(description = "게시글 ID") @PathVariable Long postId, // postId는 경로에 있지만 직접 사용되지는 않음
            @Parameter(description = "댓글 ID") @PathVariable Long commentId,
            @RequestBody PostCommentRequest requestDto) { // UserDetails 파라미터 제거
        // 임시 작성자 이름으로 "dev_user"를 사용
        PostCommentResponse updatedComment = commentService.updateComment(commentId, requestDto, "dev_user");
        return ResponseEntity.ok(ApiResponse.success(updatedComment));
    }

    @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다.")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @Parameter(description = "게시글 ID") @PathVariable Long postId, // postId는 경로에 있지만 직접 사용되지는 않음
            @Parameter(description = "댓글 ID") @PathVariable Long commentId) { // UserDetails 파라미터 제거
        // 임시 작성자 이름으로 "dev_user"를 사용
        commentService.deleteComment(commentId, "dev_user");
        return ResponseEntity.ok(ApiResponse.success());
    }
}
