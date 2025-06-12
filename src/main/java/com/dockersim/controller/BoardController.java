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

import com.dockersim.dto.request.BoardRequest;
import com.dockersim.dto.response.BoardResponse;
import com.dockersim.service.BoardService;

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

@Tag(name = "Board", description = "게시판 API")
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "게시글 생성", description = "새로운 게시글을 작성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "게시글 생성 성공", 
            content = @Content(schema = @Schema(implementation = BoardResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<BoardResponse> createBoard(@Valid @RequestBody BoardRequest requestDto) {
        log.info("게시글 생성 요청");
        Long userId = getLoginUserId();
        BoardResponse responseDto = boardService.createBoard(requestDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 게시글 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getBoard(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long id) {
        log.info("게시글 상세 조회 요청: id={}", id);
        BoardResponse responseDto = boardService.getBoard(id);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "게시글 목록 조회", description = "모든 게시글 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<BoardResponse>> getBoardList() {
        log.info("게시글 목록 조회 요청");
        List<BoardResponse> boards = boardService.getBoardList();
        return ResponseEntity.ok(boards);
    }
    
    @Operation(summary = "게시글 페이징 조회", description = "페이징 처리된 게시글 목록을 조회합니다.")
    @GetMapping("/page")
    public ResponseEntity<Page<BoardResponse>> getBoardPage(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        log.info("게시글 페이징 조회 요청: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        Page<BoardResponse> boardPage = boardService.getBoardPage(pageable);
        return ResponseEntity.ok(boardPage);
    }
    
    @Operation(summary = "게시글 타입별 조회", description = "특정 타입의 게시글 목록을 조회합니다.")
    @GetMapping("/type/{type}")
    public ResponseEntity<List<BoardResponse>> getBoardsByType(
            @Parameter(description = "게시글 타입", required = true) @PathVariable String type) {
        log.info("게시글 타입별 조회 요청: type={}", type);
        List<BoardResponse> boards = boardService.getBoardsByType(type);
        return ResponseEntity.ok(boards);
    }
    
    @Operation(summary = "게시글 검색", description = "제목이나 내용에 키워드를 포함하는 게시글을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<List<BoardResponse>> searchBoards(
            @Parameter(description = "검색 키워드", required = true) 
            @RequestParam String keyword) {
        log.info("게시글 검색 요청: keyword={}", keyword);
        List<BoardResponse> boards = boardService.searchBoards(keyword);
        return ResponseEntity.ok(boards);
    }

    @Operation(summary = "게시글 수정", description = "게시글 ID로 게시글을 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BoardResponse> updateBoard(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long id,
            @Valid @RequestBody BoardRequest requestDto
    ) {
        log.info("게시글 수정 요청: id={}", id);
        Long userId = getLoginUserId();
        BoardResponse responseDto = boardService.updateBoard(id, requestDto, userId);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "게시글 삭제", description = "게시글 ID로 게시글을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long id) {
        log.info("게시글 삭제 요청: id={}", id);
        Long userId = getLoginUserId();
        boardService.deleteBoard(id, userId);
        return ResponseEntity.noContent().build();
    }
    
    // 임시 메소드: 실제 구현에서는 인증/인가 로직과 연결해야 함
    private Long getLoginUserId() {
        // 테스트용으로 고정된 사용자 ID 반환
        // 실제 구현에서는 JWT 토큰이나 세션에서 사용자 ID를 추출해야 함
        return 1L;
    }
}
