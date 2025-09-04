package com.dockersim.service.user;

import com.dockersim.domain.User;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.UserErrorCode;
import com.dockersim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFinderImpl implements UserFinder {

    private final UserRepository repo;

    @Override
    public User findUserById(Long id) {
        return repo.findById(id).orElseThrow(
            () -> new BusinessException(UserErrorCode.USER_NOT_FOUND, id));
    }

    @Override
    public User findUserByPublicId(String publicId) {
        return repo.findByPublicId(publicId).orElseThrow(
            () -> new BusinessException(UserErrorCode.USER_NOT_FOUND, publicId));
    }

    @Override
    public User findUserByEmail(String email) {
        return repo.findByEmail(email)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_EMAIL_NOT_FOUND, email));
    }
}
