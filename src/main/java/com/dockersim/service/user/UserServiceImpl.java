package com.dockersim.service.user;

import com.dockersim.domain.User;
import com.dockersim.dto.request.UserRequest;
import com.dockersim.dto.response.UserResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.UserErrorCode;
import com.dockersim.repository.UserRepository;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    // 이메일 유효성 검증을 위한 정규표현식
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private final UserRepository repo;


    @Transactional
    public UserResponse createUser(UserRequest request) {
        validateEmailFormat(request.getEmail());
        validateEmailDuplication(request.getEmail());

        User savedUser = repo.save(User.fromUserRequest(request));
        return UserResponse.from(savedUser);
    }


    public UserResponse getUser(UUID id) {
        User user = repo.findByUserId(id)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND, id));

        return UserResponse.from(user);
    }

    public void deleteUser(UUID id) {
        User user = repo.findByUserId(id)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND, id));

        repo.delete(user);
    }

    private void validateEmailFormat(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException(UserErrorCode.INVALID_EMAIL_FORMAT, email);
        }
    }

    private void validateEmailDuplication(String email) {
        if (repo.existsByEmail(email)) {
            throw new BusinessException(UserErrorCode.EMAIL_ALREADY_EXISTS, email);
        }
    }
}