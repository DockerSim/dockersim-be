package com.dockersim.controller;

import com.dockersim.domain.enums.PostType;
import com.dockersim.dto.request.PostRequest;
import com.dockersim.dto.response.PostResponse;
import com.dockersim.service.PostService;
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

@Tag(name = "Post API", description = "게시글 관련 API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 생성", description = "새로운 게시글을 작성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @RequestBody PostRequest requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        PostResponse response = postService.createPost(requestDto, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "모든 게시글 조회", description = "키워드와 게시글 타입으로 게시글을 필터링하여 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPosts(
            @Parameter(description = "검색할 키워드") @RequestParam(required = false) String keyword,
            @Parameter(description = "게시글 타입 (QUESTION, SIMULATION, TECHNICAL)") @RequestParam(required = false) PostType type) {
        List<PostResponse> responses = postService.readAllPosts(keyword, type);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(summary = "특정 게시글 조회", description = "게시글 ID로 특정 게시글을 조회합니다.")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable Long postId) {
        PostResponse response = postService.readPost(postId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "게시글 수정", description = "게시글 ID로 특정 게시글을 수정합니다.")
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @PathVariable Long postId,
            @RequestBody PostRequest requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        PostResponse response = postService.updatePost(postId, requestDto, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "게시글 삭제", description = "게시글 ID로 특정 게시글을 삭제합니다.")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(postId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "게시글 좋아요 토글", description = "게시글에 좋아요를 누르거나 취소합니다.")
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Void>> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        postService.toggleLike(postId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "게시글 좋아요 수 조회", description = "특정 게시글의 좋아요 수를 조회합니다.")
    @GetMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<Integer>> getLikesCount(@PathVariable Long postId) {
        int likesCount = postService.getLikesCount(postId);
        return ResponseEntity.ok(ApiResponse.success(likesCount));
    }
}