package com.dockersim.service;

import com.dockersim.domain.Post;
import com.dockersim.domain.PostLike;
import com.dockersim.domain.User;
import com.dockersim.domain.enums.PostType;
import com.dockersim.dto.request.PostRequest;
import com.dockersim.dto.response.PostResponse;
import com.dockersim.repository.PostLikeRepository;
import com.dockersim.repository.PostRepository;
import com.dockersim.repository.UserRepository; // UserRepository 추가
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository; // UserFinder 대신 UserRepository 주입

    // 게시글 작성
    @Transactional
    public PostResponse createPost(PostRequest requestDto, String userPublicId) {
        User user = userRepository.findByPublicId(userPublicId) // UserFinder 대신 UserRepository 사용
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Post post = new Post(
            requestDto.getTitle(),
            requestDto.getContent(),
            user,
            requestDto.getType(),
            requestDto.getTags()
        );
        postRepository.save(post);
        return PostResponse.from(post, 0);
    }

    // 게시글 조회
    @Transactional
    public PostResponse readPost(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        
        post.setViews(post.getViews() + 1); // 조회수 증가
        
        long likesCount = postLikeRepository.countByPostId(postId); // 좋아요 수 계산
        
        return PostResponse.from(post, (int) likesCount);
    }
    
    // 필터링에 따른 모든 게시글 조회
    public List<PostResponse> readAllPosts(String keyword, PostType type) {
        List<Post> posts;
        if (keyword != null && !keyword.isBlank()) {
            // 키워드가 있으면 제목, 내용, 태그로 검색
            posts = postRepository.findByTitleContainingOrContentContainingOrTagsContaining(keyword, keyword, keyword);
        } else if (type != null) {
            posts = postRepository.findByType(type);
        } else {
            posts = postRepository.findAllByOrderByCreatedAtDesc();
        }

        // 중복 제거 및 좋아요 수 계산
        return posts.stream()
            .distinct()
            .map(post -> {
                long likesCount = postLikeRepository.countByPostId(post.getId());
                return PostResponse.from(post, (int) likesCount);
            })
            .collect(Collectors.toList());
    }

    // 게시글 수정
    @Transactional
    public PostResponse updatePost(Long postId, PostRequest requestDto, String userPublicId) {
        User user = userRepository.findByPublicId(userPublicId) // UserFinder 대신 UserRepository 사용
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        
        if (!post.getAuthor().getPublicId().equals(user.getPublicId())) {
            throw new IllegalArgumentException("게시글 수정 권한이 없습니다.");
        }
        
        post.setTitle(requestDto.getTitle());
        post.setContent(requestDto.getContent());
        post.setType(requestDto.getType());
        post.setTags(requestDto.getTags());
        
        long likesCount = postLikeRepository.countByPostId(postId);
        return PostResponse.from(post, (int) likesCount);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId, String userPublicId) {
        User user = userRepository.findByPublicId(userPublicId) // UserFinder 대신 UserRepository 사용
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getAuthor().getPublicId().equals(user.getPublicId())) {
            throw new IllegalArgumentException("게시글 삭제 권한이 없습니다.");
        }
        
        postRepository.delete(post);
    }
    
    // 게시글에 좋아요 누르기 / 취소하기
    @Transactional
    public void toggleLike(Long postId, String userPublicId) {
        User user = userRepository.findByPublicId(userPublicId) // UserFinder 대신 UserRepository 사용
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Optional<PostLike> existingLike = postLikeRepository.findByAuthorAndPost(user, post);
        
        if (existingLike.isPresent()) {
            // 이미 좋아요를 눌렀으면 취소
            postLikeRepository.delete(existingLike.get());
        } else {
            // 좋아요를 누르지 않았으면 등록
            PostLike newLike = new PostLike(user, post);
            postLikeRepository.save(newLike);
        }
    }

    // 내가 쓴 게시글 목록 조회
    public List<PostResponse> getPostsByAuthor(String userPublicId) {
        User user = userRepository.findByPublicId(userPublicId) // UserFinder 대신 UserRepository 사용
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        List<Post> posts = postRepository.findByAuthor(user);
        return posts.stream()
            .map(post -> {
                long likesCount = postLikeRepository.countByPostId(post.getId());
                return PostResponse.from(post, (int) likesCount);
            })
            .collect(Collectors.toList());
    }

    // 내가 누른 좋아요 조회
    public List<PostResponse> getLikedPostsByAuthor(String userPublicId) {
        User user = userRepository.findByPublicId(userPublicId) // UserFinder 대신 UserRepository 사용
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        List<PostLike> likedPosts = postLikeRepository.findByAuthor(user);
        return likedPosts.stream()
            .map(postLike -> {
                Post post = postLike.getPost();
                long likesCount = postLikeRepository.countByPostId(post.getId());
                return PostResponse.from(post, (int) likesCount);
            })
            .collect(Collectors.toList());
    }

    public int getLikesCount(Long postId) {
        return (int) postLikeRepository.countByPostId(postId);
    }
}
