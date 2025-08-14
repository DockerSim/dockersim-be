package com.dockersim.service;

import com.dockersim.domain.CommunityComments;
import com.dockersim.domain.CommunityPost;
import com.dockersim.dto.CommentRequest;
import com.dockersim.dto.CommentResponse;
import com.dockersim.repository.PostCommentRepository;
import com.dockersim.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final PostCommentRepository commentRepository;
    private final PostRepository postRepository;
    
    // 댓글 작성
    @Transactional
    public CommentResponse createComment(CommentRequest requestDto, String author) {
        Post post = postRepository.findById(requestDto.getPostId())
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
            
        Comment comment = new Comment(
            requestDto.getContent(),
            author,
            post
        );
        commentRepository.save(comment);
        return new CommentResponse(comment);
    }
    
    // 특정 게시글의 댓글 최신순으로 조회
    public List<CommentResponse> readCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
        return comments.stream()
            .map(CommentResponse::new)
            .collect(Collectors.toList());
    }
    
    // 댓글 수정
    @Transactional
    public CommentResponse updateComment(Long commentId, CommentRequest requestDto, String author) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
            
        if (!comment.getAuthor().equals(author)) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }
        
        comment.setContent(requestDto.getContent());
        return new CommentResponse(comment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, String author) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getAuthor().equals(author)) {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }
    
    // 내가 작성한 댓글 목록 조회
    public List<CommentResponse> getCommentsByAuthor(String author) {
        List<Comment> comments = commentRepository.findByAuthorOrderByCreatedAtDesc(author);
        return comments.stream()
            .map(CommentResponse::new)
            .collect(Collectors.toList());
    }
}
