package com.dockersim.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.dockersim.dto.request.BoardRequest;
import com.dockersim.dto.response.BoardResponse;
import com.dockersim.entity.Board;
import com.dockersim.entity.User;
import com.dockersim.repository.CommentRepository;
import com.dockersim.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BoardMapper {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    /**
     * BoardRequest DTO를 Board Entity로 변환
     * @param dto 게시글 요청 DTO
     * @param userId 작성자 ID
     * @return Board 엔티티
     */
    public Board toEntity(BoardRequest dto, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId));

        return Board.builder()
                .simulationId(dto.getSimulationId())
                .user(user)
                .title(dto.getTitle())
                .content(dto.getContent())
                .tags(String.join(",", dto.getTags()))
                .type(dto.getType())
                .build();
    }

    /**
     * Board Entity를 BoardResponse DTO로 변환
     * @param board 게시글 엔티티
     * @return BoardResponse DTO
     */
    public BoardResponse toDto(Board board) {
        // 태그 문자열을 리스트로 변환
        List<String> tagList = board.getTags() != null && !board.getTags().isEmpty()
                ? Arrays.asList(board.getTags().split(","))
                : List.of();
        
        // 댓글 수 조회
        int commentCount = (int) commentRepository.countByBoardId(board.getId());
        
        return BoardResponse.builder()
                .id(board.getId())
                .simulationId(board.getSimulationId())
                .userId(board.getUser().getId())
                .userName(board.getUser().getUsername()) // User 엔티티의 username 필드 사용
                .title(board.getTitle())
                .content(board.getContent())
                .tags(tagList)
                .type(board.getType())
                .likeCount(board.getLikeCount())
                .commentCount(commentCount)
                .createdAt(board.getCreatedAt())
                .build();
    }
    
    /**
     * Board 엔티티 리스트를 BoardResponse DTO 리스트로 변환
     * @param boards 게시글 엔티티 리스트
     * @return BoardResponse DTO 리스트
     */
    public List<BoardResponse> toDtoList(List<Board> boards) {
        return boards.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
