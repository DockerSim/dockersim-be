package com.dockersim.mapper;

import org.springframework.stereotype.Component;

import com.dockersim.dto.request.CommentRequest;
import com.dockersim.dto.response.CommentAdminResponse;
import com.dockersim.dto.response.CommentResponse;
import com.dockersim.entity.Board;
import com.dockersim.entity.Comment;
import com.dockersim.entity.User;
import com.dockersim.repository.BoardRepository;
import com.dockersim.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    /**
     * CommentRequest DTO를 Comment Entity로 변환
     * @param dto 댓글 요청 DTO
     * @param userId 작성자 ID
     * @return Comment 엔티티
     */
    public Comment toEntity(CommentRequest dto, Long userId) {
        Board board = boardRepository.findById(dto.getBoardId())
            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. ID: " + dto.getBoardId()));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId));
        
        return Comment.builder()
                .board(board)
                .user(user)
                .content(dto.getContent())
                .isAnonymous(dto.getIsAnonymous())
                .build();
    }

    /**
     * Comment Entity를 일반 사용자용 CommentResponse DTO로 변환
     * @param comment 댓글 엔티티
     * @return CommentResponse DTO
     */
    public CommentResponse toDto(Comment comment) {
        String userName = comment.getIsAnonymous() ? "익명 사용자" : comment.getUser().getUsername();
        
        return CommentResponse.builder()
                .id(comment.getId())
                .boardId(comment.getBoard().getId())
                .userName(userName)
                .content(comment.getContent())
                .isAnonymous(comment.getIsAnonymous())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    /**
     * Comment Entity를 관리자용 CommentAdminResponse DTO로 변환
     * @param comment 댓글 엔티티
     * @return CommentAdminResponse DTO
     */
    public CommentAdminResponse toAdminDto(Comment comment) {
        return CommentAdminResponse.builder()
                .id(comment.getId())
                .boardId(comment.getBoard().getId())
                .userId(comment.getUser().getId())
                .userName(comment.getUser().getUsername())
                .userEmail(comment.getUser().getEmail())
                .content(comment.getContent())
                .isAnonymous(comment.getIsAnonymous())
                .createdAt(comment.getCreatedAt())
                .build();
    }
    
    /**
     * Comment 엔티티 리스트를 CommentResponse DTO 리스트로 변환
     * @param comments 댓글 엔티티 리스트
     * @return CommentResponse DTO 리스트
     */
    public List<CommentResponse> toDtoList(List<Comment> comments) {
        return comments.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Comment 엔티티 리스트를 관리자용 CommentAdminResponse DTO 리스트로 변환
     * @param comments 댓글 엔티티 리스트
     * @return CommentAdminResponse DTO 리스트
     */
    public List<CommentAdminResponse> toAdminDtoList(List<Comment> comments) {
        return comments.stream()
                .map(this::toAdminDto)
                .collect(Collectors.toList());
    }
}
