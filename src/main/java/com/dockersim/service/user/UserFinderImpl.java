package com.dockersim.service.user;

import com.dockersim.domain.User;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.UserErrorCode;
import com.dockersim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFinderImpl implements UserFinder {

    private final UserRepository userRepository;

    @Override
    public User findUserByUserId(String userId) {
        return userRepository.findByUserId(userId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND, userId));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_EMAIL_NOT_FOUND, email));
    }
}
