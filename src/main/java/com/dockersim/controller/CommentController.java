package com.dockersim.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dockersim.dto.request.CommentRequest;
import com.dockersim.dto.response.CommentAdminResponse;
import com.dockersim.dto.response.CommentResponse;
import com.dockersim.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Comment", description = "댓글 API")
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;
    
    @Operation(summary = "댓글 생성", description = "새로운 댓글을 작성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "댓글 생성 성공", 
            content = @Content(schema = @Schema(implementation = CommentResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CommentRequest requestDto) {
        log.info("댓글 생성 요청: boardId={}", requestDto.getBoardId());
        Long userId = getLoginUserId();
        CommentResponse responseDto = commentService.createComment(requestDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
    
    @Operation(summary = "게시글 댓글 목록 조회", description = "게시글에 달린 댓글 목록을 조회합니다.")
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByBoardId(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long boardId) {
        log.info("게시글 댓글 목록 조회 요청: boardId={}", boardId);
        List<CommentResponse> comments = commentService.getCommentsByBoardId(boardId);
        return ResponseEntity.ok(comments);
    }
    
    @Operation(summary = "게시글 댓글 페이징 조회", description = "게시글에 달린 댓글을 페이징 처리하여 조회합니다.")
    @GetMapping("/board/{boardId}/page")
    public ResponseEntity<Page<CommentResponse>> getCommentsByBoardIdPaging(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long boardId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        log.info("게시글 댓글 페이징 조회 요청: boardId={}, page={}, size={}", 
                boardId, pageable.getPageNumber(), pageable.getPageSize());
        Page<CommentResponse> comments = commentService.getCommentsByBoardId(boardId, pageable);
        return ResponseEntity.ok(comments);
    }
    
    @Operation(summary = "관리자용 댓글 목록 조회", description = "관리자가 게시글에 달린 모든 댓글 정보를 조회합니다.")
    @GetMapping("/admin/board/{boardId}")
    public ResponseEntity<List<CommentAdminResponse>> getCommentsByBoardIdForAdmin(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long boardId) {
        log.info("관리자용 게시글 댓글 목록 조회 요청: boardId={}", boardId);
        List<CommentAdminResponse> comments = commentService.getCommentsByBoardIdForAdmin(boardId);
        return ResponseEntity.ok(comments);
    }
    
    @Operation(summary = "댓글 상세 조회", description = "댓글 ID로 댓글 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> getComment(
            @Parameter(description = "댓글 ID", required = true) @PathVariable Long id) {
        log.info("댓글 상세 조회 요청: id={}", id);
        CommentResponse responseDto = commentService.getComment(id);
        return ResponseEntity.ok(responseDto);
    }
    
    @Operation(summary = "댓글 수정", description = "댓글 ID로 댓글 내용을 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> updateComment(
            @Parameter(description = "댓글 ID", required = true) @PathVariable Long id,
            @Parameter(description = "댓글 내용", required = true) @RequestParam String content) {
        log.info("댓글 수정 요청: id={}", id);
        Long userId = getLoginUserId();
        CommentResponse responseDto = commentService.updateComment(id, content, userId);
        return ResponseEntity.ok(responseDto);
    }
    
    @Operation(summary = "댓글 삭제", description = "댓글 ID로 댓글을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "댓글 ID", required = true) @PathVariable Long id) {
        log.info("댓글 삭제 요청: id={}", id);
        Long userId = getLoginUserId();
        commentService.deleteComment(id, userId);
        return ResponseEntity.noContent().build();
    }
    
    // 임시 메소드: 실제 구현에서는 인증/인가 로직과 연결해야 함
    private Long getLoginUserId() {
        // 테스트용으로 고정된 사용자 ID 반환
        // 실제 구현에서는 JWT 토큰이나 세션에서 사용자 ID를 추출해야 함
        return 1L;
    }
} 