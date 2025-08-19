package com.dockersim.controller;

import com.dockersim.domain.enums.PostType;
import com.dockersim.dto.request.PostRequest;
import com.dockersim.dto.response.PostResponse;
import com.dockersim.exception.code.CommunityErrorCode;
import com.dockersim.service.PostService;
import com.dockersim.web.ApiResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // ApiResponse를 활용하여 API 응답 형식 통일.
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(@Valid @RequestBody PostRequest requestDto) {
        // 서비스는 기존과 동일하게 PostResponse를 반환.
        PostResponse response = postService.createPost(requestDto, "author_name");
        
        // 컨트롤러에서 ApiResponse.ok()를 사용하여 통일 응답.
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // 모든 게시글 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) PostType type) {
        List<PostResponse> responses = postService.readAllPosts(keyword, type);
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostRequest requestDto) {
        PostResponse response = postService.updatePost(postId, requestDto);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // 게시글 좋아요
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Void>> likePost(@PathVariable Long postId) {
        postService.likePost(postId, "author_name");
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // 게시글 좋아요 취소
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Void>> unlikePost(@PathVariable Long postId) {
        postService.unlikePost(postId, "author_name");
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // 게시글 필터링 조회
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<PostResponse>>> filterPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) PostType type) {
        List<PostResponse> responses = postService.readAllPosts(keyword, type);
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }
    
    // 게시글 조회
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable Long postId) {
        PostResponse response = postService.readPost(postId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
    
    // 만약 예외가 발생하면 다음과 같이 ApiResponse.error()를 사용할 수 있음.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(ApiResponse.error(CommunityErrorCode.INVALID_INPUT, e.getMessage()));
    }
}
