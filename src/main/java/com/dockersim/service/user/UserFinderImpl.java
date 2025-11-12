package com.dockersim.service.user;

import com.dockersim.domain.User;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.UserErrorCode;
import com.dockersim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Slf4j 임포트
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j // Slf4j 어노테이션 추가
public class UserFinderImpl implements UserFinder {

    private final UserRepository repo;

    @Override
    public User findUserById(Long id) {
        return repo.findById(id).orElseThrow(
            () -> new BusinessException(UserErrorCode.USER_NOT_FOUND, id));
    }

    @Override
    public User findUserByPublicId(String publicId) {
        log.info("UserFinderImpl: Attempting to find user by publicId: {}", publicId); // 로그 추가
        return repo.findByPublicId(publicId).orElseThrow(
            () -> {
                log.error("UserFinderImpl: User not found for publicId: {}", publicId); // 에러 로그 추가
                return new BusinessException(UserErrorCode.USER_NOT_FOUND, publicId);
            });
    }

    @Override
    public User findUserByEmail(String email) {
        return repo.findByEmail(email)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_EMAIL_NOT_FOUND, email));
    }
}
