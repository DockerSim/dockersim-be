package com.dockersim.integration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dockersim.dto.request.UserRequest;
import com.dockersim.dto.response.UserResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.UserErrorCode;
import com.dockersim.integration.IntegrationTestSupport;
import com.dockersim.repository.UserRepository;
import com.dockersim.service.user.UserService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("UserService 통합 테스트")
class UserServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("사용자 생성 및 조회 통합 테스트")
    void createUserAndGetUser() {
        // given
        UserRequest request = new UserRequest();
        request.setEmail("test@example.com");
        request.setName("Test User");

        // when
        UserResponse createdUser = userService.createUser(request);

        // then
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo(request.getEmail());

        // when
        UserResponse foundUser = userService.getUser(createdUser.getUserId());

        // then
        assertThat(foundUser)
            .usingRecursiveComparison()
            .isEqualTo(createdUser);
    }

    @Test
    @DisplayName("이메일 중복 체크 통합 테스트")
    void duplicateEmailCheck() {
        // given
        UserRequest request = new UserRequest();
        request.setEmail("test@example.com");
        request.setName("Test User");
        userService.createUser(request);

        // when & then
        assertThatThrownBy(() -> userService.createUser(request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.EMAIL_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("잘못된 이메일 형식으로 사용자 생성 시 실패")
    void createUserWithInvalidEmail() {
        // given
        UserRequest request = new UserRequest();
        request.setEmail("invalid-email");
        request.setName("Test User");

        // when & then
        assertThatThrownBy(() -> userService.createUser(request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.INVALID_EMAIL_FORMAT);
    }

    @Test
    @DisplayName("사용자 삭제 통합 테스트")
    void deleteUser() {
        // given
        UserRequest request = new UserRequest();
        request.setEmail("test@example.com");
        request.setName("Test User");
        UserResponse createdUser = userService.createUser(request);

        // when
        userService.deleteUser(createdUser.getUserId());

        // then
        // FIX: getId() 대신 getUserId()를 사용하고, findByUserId로 조회
        assertThat(userRepository.findByUserId(createdUser.getUserId())).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 사용자 조회 시 실패")
    void getUserNotFound() {
        assertThatThrownBy(() -> userService.getUser(UUID.randomUUID().toString()))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 삭제 시 실패")
    void deleteUserNotFound() {
        assertThatThrownBy(() -> userService.deleteUser(UUID.randomUUID().toString()))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.USER_NOT_FOUND);
    }
}
