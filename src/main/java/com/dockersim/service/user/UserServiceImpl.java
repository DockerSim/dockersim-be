package com.dockersim.service.user;

import com.dockersim.domain.User;
import com.dockersim.dto.request.UserRequest;
import com.dockersim.dto.response.UserResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.UserErrorCode;
import com.dockersim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.regex.Pattern;

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
    @Override
    public UserResponse createUser(UserRequest request) {
        validateEmailFormat(request.getEmail());
        validateEmailDuplication(request.getEmail());

        User savedUser = repo.save(User.fromUserRequest(request));
        return UserResponse.from(savedUser);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse getUser(UUID id) {
        return UserResponse.from(findUserByUUID(id));
    }

    @Override
    public void deleteUser(UUID id) {
        repo.delete(findUserByUUID(id));
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

    @Override
    public User findUserByUUID(UUID userId) {
        return repo.findByUserId(userId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND, userId));
    }

    @Override
    public User findUserByEmail(String email) {
        return repo.findByEmail(email)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_EMAIL_NOT_FOUND, email));
    }

    @Override // 인터페이스의 메소드를 구현한다는 것을 명시 (가독성 및 안정성 증가)
    public void updateEmail(UUID userId, String newEmail) {
        // 1. DB에서 현재 사용자 정보를 가져옵니다.
        User currentUser = repo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")); // 실제로는 커스텀 예외 사용 권장

        // 2. (중요) 새로 입력된 이메일이 다른 사용자에 의해 이미 사용 중인지 확인합니다.
        repo.findByEmail(newEmail).ifPresent(user -> {
            if (!user.getUserId().equals(userId)) {
                throw new RuntimeException("이미 사용 중인 이메일입니다."); // 커스텀 예외 사용 권장
            }
        });

        // 3. 사용자 엔티티의 이메일 정보를 업데이트합니다.
        currentUser.updateEmail(newEmail);

        // 4. @Transactional에 의해 메소드가 끝나면 자동으로 DB에 저장(update)됩니다.
    }
}