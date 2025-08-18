package com.dockersim.integration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dockersim.dto.request.UserRequest;
import com.dockersim.dto.response.UserResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.UserErrorCode;
import com.dockersim.repository.UserRepository;
import com.dockersim.service.user.UserService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayName("UserService 통합 테스트")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UserRequest testRequest;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testRequest = new UserRequest();
        testRequest.setEmail("test@example.com");
        testRequest.setName("Test User");
    }

    @Test
    @DisplayName("사용자 생성 및 조회 통합 테스트")
    void createAndGetUser() {
        // when - 사용자 생성
        UserResponse createResponse = userService.createUser(testRequest);

        // then - 생성 결과 검증
        assertThat(createResponse).isNotNull();
        assertThat(createResponse.getEmail()).isEqualTo(testRequest.getEmail());
        assertThat(createResponse.getName()).isEqualTo(testRequest.getName());
        assertThat(createResponse.getUserId()).isNotNull();

        // when - 생성된 사용자 조회
        UserResponse getResponse = userService.getUser(createResponse.getUserId());

        // then - 조회 결과 검증
        assertThat(getResponse)
            .usingRecursiveComparison()
            .isEqualTo(createResponse);
    }

    @Test
    @DisplayName("잘못된 이메일 형식으로 사용자 생성 시 실패")
    void createUser_WithInvalidEmail() {
        // given
        testRequest.setEmail("invalid-email");

        // when & then
        assertThatThrownBy(() -> userService.createUser(testRequest))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.INVALID_EMAIL_FORMAT);

        // DB 검증
        assertThat(userRepository.count()).isZero();
    }

    @Test
    @DisplayName("이메일 중복 체크 통합 테스트")
    void duplicateEmailCheck() {
        // given
        userService.createUser(testRequest);

        UserRequest duplicateRequest = new UserRequest();
        duplicateRequest.setEmail(testRequest.getEmail());
        duplicateRequest.setName("Another User");

        // when & then
        assertThatThrownBy(() -> userService.createUser(duplicateRequest))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.EMAIL_ALREADY_EXISTS);

        // DB 검증
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("사용자 삭제 통합 테스트")
    void deleteUser() {
        // given
        UserResponse createResponse = userService.createUser(testRequest);
        assertThat(userRepository.count()).isEqualTo(1);

        // when
        userService.deleteUser(createResponse.getUserId());

        // then
        assertThat(userRepository.count()).isZero();
        assertThat(userRepository.findByUserId(createResponse.getUserId())).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 사용자 조회 시 실패")
    void getUser_NotFound() {
        // given
        UUID nonExistentId = UUID.randomUUID();

        // when & then
        assertThatThrownBy(() -> userService.getUser(nonExistentId))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 삭제 시 실패")
    void deleteUser_NotFound() {
        // given
        UUID nonExistentId = UUID.randomUUID();

        // when & then
        assertThatThrownBy(() -> userService.deleteUser(nonExistentId))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.USER_NOT_FOUND);
    }
}
