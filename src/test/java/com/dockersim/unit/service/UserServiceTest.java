package com.dockersim.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.dockersim.domain.User;
import com.dockersim.dto.request.UserRequest;
import com.dockersim.dto.response.UserResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.UserErrorCode;
import com.dockersim.repository.UserRepository;
import com.dockersim.service.user.UserFinder;
import com.dockersim.service.user.UserServiceImpl;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock // FIX: UserFinder Mock 객체 추가
    private UserFinder userFinder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("createUser - 성공")
    void createUser_Success() {
        // given
        UserRequest request = new UserRequest();
        request.setEmail("test@example.com");
        request.setName("Test User");

        User user = User.fromUserRequest(request);
        given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        UserResponse response = userService.createUser(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
        assertThat(response.getName()).isEqualTo(request.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("createUser - 잘못된 이메일 형식")
    void createUser_InvalidEmailFormat() {
        // given
        UserRequest request = new UserRequest();
        request.setEmail("invalid-email");
        request.setName("Test User");

        // when & then
        assertThatThrownBy(() -> userService.createUser(request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.INVALID_EMAIL_FORMAT);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("createUser - 이메일 중복")
    void createUser_DuplicateEmail() {
        // given
        UserRequest request = new UserRequest();
        request.setEmail("test@example.com");
        request.setName("Test User");

        given(userRepository.existsByEmail(request.getEmail())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.createUser(request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.EMAIL_ALREADY_EXISTS);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("getUser - 성공")
    void getUser_Success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
            .userId(userId)
            .email("test@example.com")
            .name("Test User")
            .build();

        // FIX: userRepository 대신 userFinder를 사용하도록 수정
        given(userFinder.findUserByUUID(userId)).willReturn(user);

        // when
        UserResponse response = userService.getUser(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getEmail()).isEqualTo(user.getEmail());
        assertThat(response.getName()).isEqualTo(user.getName());
    }

    @Test
    @DisplayName("getUser - 사용자 없음")
    void getUser_UserNotFound() {
        // given
        UUID userId = UUID.randomUUID();
        // FIX: userRepository 대신 userFinder를 사용하도록 수정
        given(userFinder.findUserByUUID(userId)).willThrow(new BusinessException(UserErrorCode.USER_NOT_FOUND, userId.toString()));

        // when & then
        assertThatThrownBy(() -> userService.getUser(userId))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("deleteUser - 성공")
    void deleteUser_Success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
            .userId(userId)
            .email("test@example.com")
            .name("Test User")
            .build();

        // FIX: userRepository 대신 userFinder를 사용하도록 수정
        given(userFinder.findUserByUUID(userId)).willReturn(user);

        // when
        userService.deleteUser(userId);

        // then
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("deleteUser - 사용자 없음")
    void deleteUser_UserNotFound() {
        // given
        UUID userId = UUID.randomUUID();
        // FIX: userRepository 대신 userFinder를 사용하도록 수정
        given(userFinder.findUserByUUID(userId)).willThrow(new BusinessException(UserErrorCode.USER_NOT_FOUND, userId.toString()));

        // when & then
        assertThatThrownBy(() -> userService.deleteUser(userId))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.USER_NOT_FOUND);

        verify(userRepository, never()).delete(any());
    }
}
