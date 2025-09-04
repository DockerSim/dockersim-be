package com.dockersim.service.user;

import com.dockersim.domain.User;
import com.dockersim.dto.request.UserRequest;
import com.dockersim.dto.response.UserResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.UserErrorCode;
import com.dockersim.repository.UserRepository;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private final UserRepository userRepository;
    private final UserFinder userFinder; // UserFinder 주입


    @Override
    public UserResponse createUser(UserRequest request) {
        validateEmailFormat(request.getEmail());
        validateEmailDuplication(request.getEmail());

        User savedUser = userRepository.save(User.fromUserRequest(request));
        return UserResponse.from(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(String id) {
        // 주입된 userFinder 사용
        return UserResponse.from(userFinder.findUserByPublicId(id));
    }

    @Override
    public void deleteUser(String id) {
        // 주입된 userFinder 사용
        userRepository.delete(userFinder.findUserByPublicId(id));
    }

    private void validateEmailFormat(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException(UserErrorCode.INVALID_EMAIL_FORMAT, email);
        }
    }

    private void validateEmailDuplication(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(UserErrorCode.EMAIL_ALREADY_EXISTS, email);
        }
    }

    // findUserByUUID, findUserByEmail 메서드 제거
}
