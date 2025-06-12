package com.dockersim.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dockersim.dto.request.BoardRequest;
import com.dockersim.dto.response.BoardResponse;
import com.dockersim.entity.Board;
import com.dockersim.entity.User;
import com.dockersim.mapper.BoardMapper;
import com.dockersim.repository.BoardRepository;
import com.dockersim.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardMapper boardMapper;

    /**
     * 게시글 생성
     * @param requestDto 게시글 생성 요청 DTO
     * @param userId 작성자 ID
     * @return 생성된 게시글 응답 DTO
     */
    @Transactional
    public BoardResponse createBoard(BoardRequest requestDto, Long userId) {
        log.info("게시글 생성 요청: userId={}, title={}", userId, requestDto.getTitle());
        
        // 사용자 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));
                
        // DTO → Entity 변환 및 저장
        Board board = boardMapper.toEntity(requestDto, userId);
        Board savedBoard = boardRepository.save(board);
        
        log.info("게시글 생성 완료: boardId={}", savedBoard.getId());
        return boardMapper.toDto(savedBoard);
    }

    /**
     * 게시글 상세 조회
     * @param boardId 게시글 ID
     * @return 게시글 상세 응답 DTO
     */
    public BoardResponse getBoard(Long boardId) {
        log.info("게시글 조회 요청: boardId={}", boardId);
        
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. ID: " + boardId));
                
        return boardMapper.toDto(board);
    }

    /**
     * 게시글 목록 조회
     * @return 게시글 목록 응답 DTO 리스트
     */
    public List<BoardResponse> getBoardList() {
        log.info("게시글 목록 조회 요청");
        
        List<Board> boards = boardRepository.findAll();
        return boardMapper.toDtoList(boards);
    }
    
    /**
     * 페이징 처리된 게시글 목록 조회
     * @param pageable 페이징 정보
     * @return 페이징 처리된 게시글 목록
     */
    public Page<BoardResponse> getBoardPage(Pageable pageable) {
        log.info("페이징 게시글 목록 조회 요청: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Board> boardPage = boardRepository.findAll(pageable);
        return boardPage.map(boardMapper::toDto);
    }
    
    /**
     * 게시글 타입별 조회
     * @param type 게시글 타입
     * @return 게시글 목록 응답 DTO 리스트
     */
    public List<BoardResponse> getBoardsByType(String type) {
        log.info("게시글 타입별 조회 요청: type={}", type);
        
        List<Board> boards = boardRepository.findByType(type);
        return boardMapper.toDtoList(boards);
    }
    
    /**
     * 키워드로 게시글 검색
     * @param keyword 검색 키워드
     * @return 게시글 목록 응답 DTO 리스트
     */
    public List<BoardResponse> searchBoards(String keyword) {
        log.info("게시글 검색 요청: keyword={}", keyword);
        
        List<Board> boards = boardRepository.findByTitleOrContentContaining(keyword);
        return boardMapper.toDtoList(boards);
    }

    /**
     * 게시글 수정
     * @param boardId 게시글 ID
     * @param requestDto 게시글 수정 요청 DTO
     * @param userId 요청자 ID
     * @return 수정된 게시글 응답 DTO
     */
    @Transactional
    public BoardResponse updateBoard(Long boardId, BoardRequest requestDto, Long userId) {
        log.info("게시글 수정 요청: boardId={}, userId={}", boardId, userId);
        
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. ID: " + boardId));

        // 작성자 본인 확인
        if (!board.getUser().getId().equals(userId)) {
            throw new IllegalStateException("게시글 작성자만 수정할 수 있습니다.");
        }

        // 게시글 정보 업데이트
        board.update(
            requestDto.getTitle(),
            requestDto.getContent(),
            String.join(",", requestDto.getTags()),
            requestDto.getType()
        );
        
        Board updatedBoard = boardRepository.save(board);
        log.info("게시글 수정 완료: boardId={}", boardId);
        
        return boardMapper.toDto(updatedBoard);
    }

    /**
     * 게시글 삭제
     * @param boardId 게시글 ID
     * @param userId 요청자 ID
     */
    @Transactional
    public void deleteBoard(Long boardId, Long userId) {
        log.info("게시글 삭제 요청: boardId={}, userId={}", boardId, userId);
        
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. ID: " + boardId));

        // 작성자 본인 확인
        if (!board.getUser().getId().equals(userId)) {
            throw new IllegalStateException("게시글 작성자만 삭제할 수 있습니다.");
        }

        boardRepository.delete(board);
        log.info("게시글 삭제 완료: boardId={}", boardId);
    }
}
