package com.dockersim.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dockersim.dto.request.CommentRequest;
import com.dockersim.dto.response.CommentAdminResponse;
import com.dockersim.dto.response.CommentResponse;
import com.dockersim.entity.Board;
import com.dockersim.entity.Comment;
import com.dockersim.entity.User;
import com.dockersim.mapper.CommentMapper;
import com.dockersim.repository.BoardRepository;
import com.dockersim.repository.CommentRepository;
import com.dockersim.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    
    /**
     * 댓글 생성
     * @param requestDto 댓글 생성 요청 DTO
     * @param userId 작성자 ID
     * @return 생성된 댓글 응답 DTO
     */
    @Transactional
    public CommentResponse createComment(CommentRequest requestDto, Long userId) {
        log.info("댓글 생성 요청: userId={}, boardId={}", userId, requestDto.getBoardId());
        
        // 게시글 존재 확인
        boardRepository.findById(requestDto.getBoardId())
            .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. ID: " + requestDto.getBoardId()));
        
        // 사용자 존재 확인
        userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));
            
        // DTO → Entity 변환 및 저장
        Comment comment = commentMapper.toEntity(requestDto, userId);
        Comment savedComment = commentRepository.save(comment);
        
        log.info("댓글 생성 완료: commentId={}", savedComment.getId());
        return commentMapper.toDto(savedComment);
    }
    
    /**
     * 게시글에 달린 댓글 목록 조회
     * @param boardId 게시글 ID
     * @return 댓글 목록 응답 DTO 리스트
     */
    public List<CommentResponse> getCommentsByBoardId(Long boardId) {
        log.info("게시글 댓글 목록 조회 요청: boardId={}", boardId);
        
        // 게시글 존재 확인
        boardRepository.findById(boardId)
            .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. ID: " + boardId));
            
        List<Comment> comments = commentRepository.findByBoardId(boardId);
        return commentMapper.toDtoList(comments);
    }
    
    /**
     * 게시글에 달린 댓글 목록 페이징 조회
     * @param boardId 게시글 ID
     * @param pageable 페이징 정보
     * @return 페이징 처리된 댓글 목록
     */
    public Page<CommentResponse> getCommentsByBoardId(Long boardId, Pageable pageable) {
        log.info("게시글 댓글 페이징 조회 요청: boardId={}, page={}, size={}", 
                boardId, pageable.getPageNumber(), pageable.getPageSize());
        
        // 게시글 존재 확인
        boardRepository.findById(boardId)
            .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. ID: " + boardId));
            
        Page<Comment> commentPage = commentRepository.findByBoardId(boardId, pageable);
        return commentPage.map(commentMapper::toDto);
    }
    
    /**
     * 관리자용 - 게시글에 달린 댓글 목록 조회
     * @param boardId 게시글 ID
     * @return 관리자용 댓글 목록 응답 DTO 리스트
     */
    public List<CommentAdminResponse> getCommentsByBoardIdForAdmin(Long boardId) {
        log.info("관리자용 게시글 댓글 목록 조회 요청: boardId={}", boardId);
        
        // 게시글 존재 확인
        boardRepository.findById(boardId)
            .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. ID: " + boardId));
            
        List<Comment> comments = commentRepository.findByBoardId(boardId);
        return commentMapper.toAdminDtoList(comments);
    }
    
    /**
     * 댓글 상세 조회
     * @param commentId 댓글 ID
     * @return 댓글 상세 응답 DTO
     */
    public CommentResponse getComment(Long commentId) {
        log.info("댓글 조회 요청: commentId={}", commentId);
        
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다. ID: " + commentId));
            
        return commentMapper.toDto(comment);
    }
    
    /**
     * 댓글 수정
     * @param commentId 댓글 ID
     * @param content 수정할 내용
     * @param userId 요청자 ID
     * @return 수정된 댓글 응답 DTO
     */
    @Transactional
    public CommentResponse updateComment(Long commentId, String content, Long userId) {
        log.info("댓글 수정 요청: commentId={}, userId={}", commentId, userId);
        
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다. ID: " + commentId));
            
        // 작성자 본인 확인
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalStateException("댓글 작성자만 수정할 수 있습니다.");
        }
        
        // 댓글 내용 업데이트
        comment.setContent(content);
        Comment updatedComment = commentRepository.save(comment);
        
        log.info("댓글 수정 완료: commentId={}", commentId);
        return commentMapper.toDto(updatedComment);
    }
    
    /**
     * 댓글 삭제
     * @param commentId 댓글 ID
     * @param userId 요청자 ID
     */
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        log.info("댓글 삭제 요청: commentId={}, userId={}", commentId, userId);
        
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다. ID: " + commentId));
            
        // 작성자 본인 또는 게시글 작성자 확인
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));
            
        Board board = comment.getBoard();
        
        if (!comment.getUser().getId().equals(userId) && !board.getUser().getId().equals(userId)) {
            throw new IllegalStateException("댓글 작성자 또는 게시글 작성자만 삭제할 수 있습니다.");
        }
        
        commentRepository.delete(comment);
        log.info("댓글 삭제 완료: commentId={}", commentId);
    }
} 