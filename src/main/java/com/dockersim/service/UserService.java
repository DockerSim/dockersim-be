package com.dockersim.service;

import com.dockersim.domain.User;
import com.dockersim.dto.request.CreateUserRequest;
import com.dockersim.dto.response.UserResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.UserErrorCode;
import com.dockersim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    // 이메일 유효성 검증을 위한 정규표현식
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * 사용자 생성
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("사용자 생성 요청: {}", request.getEmail());

        // 이메일 형식 검증
        validateEmailFormat(request.getEmail());

        // 이메일 중복 확인
        validateEmailDuplication(request.getEmail());

        // 사용자 생성 및 저장
        User user = new User(request.getName(), request.getEmail());
        User savedUser = userRepository.save(user);

        log.info("사용자 생성 완료: ID={}, Email={}", savedUser.getId(), savedUser.getEmail());
        return UserResponse.from(savedUser);
    }

    /**
     * 사용자 조회
     */
    public UserResponse getUser(Long id) {
        log.info("사용자 조회 요청: ID={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND, id));

        return UserResponse.from(user);
    }

    /**
     * 이메일 형식 검증
     */
    private void validateEmailFormat(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException(UserErrorCode.INVALID_EMAIL_FORMAT, email);
        }
    }

    /**
     * 이메일 중복 확인
     */
    private void validateEmailDuplication(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(UserErrorCode.EMAIL_ALREADY_EXISTS, email);
        }
    }
}