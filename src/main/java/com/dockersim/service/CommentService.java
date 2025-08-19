package com.dockersim.service;

import com.dockersim.domain.Comments;
import com.dockersim.domain.Post;
import com.dockersim.dto.request.PostCommentRequest;
import com.dockersim.dto.response.PostCommentResponse;
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
    public PostCommentResponse createComment(PostCommentRequest requestDto, String author) {
        Post post = postRepository.findById(requestDto.getPostId())
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
            
        Comments comment = new Comments(
            requestDto.getContent(),
            author,
            post
        );
        PostCommentRepository.save(comment);
        return new PostCommentResponse(comment);
    }
    
    // 특정 게시글의 댓글 최신순으로 조회
    public List<PostCommentResponse> readCommentsByPostId(Long postId) {
        List<Comments> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
        return comments.stream()
            .map(PostCommentResponse::new)
            .collect(Collectors.toList());
    }
    
    // 댓글 수정
    @Transactional
    public PostCommentResponse updateComment(Long commentId, PostCommentRequest requestDto, String author) {
        Comments comment = PostCommentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
            
        if (!comment.getAuthor().equals(author)) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }
        
        comment.setContent(requestDto.getContent());
        return new PostCommentResponse(comment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, String author) {
        Comments comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getAuthor().equals(author)) {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
        }

        PostCommentRepository.delete(comment);
    }
    
    // 내가 작성한 댓글 목록 조회
    public List<PostCommentResponse> getCommentsByAuthor(String author) {
        List<Comments> comments = commentRepository.findByAuthorOrderByCreatedAtDesc(author);
        return comments.stream()
            .map(PostCommentResponse::new)
            .collect(Collectors.toList());
    }
}
