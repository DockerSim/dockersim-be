package com.dockersim.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dockersim.domain.User;
import com.dockersim.dto.request.UserRequest;
import com.dockersim.dto.response.UserResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.UserErrorCode;
import com.dockersim.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequest testRequest;
    private User testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testRequest = new UserRequest();
        testRequest.setEmail("test@example.com");
        testRequest.setName("Test User");

        testUser = User.fromUserRequest(testRequest);
    }

    @Test
    @DisplayName("사용자 생성 - 성공")
    void createUser_Success() {
        // given
        when(userRepository.existsByEmail(testRequest.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        UserResponse response = userService.createUser(testRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(testRequest.getEmail());
        assertThat(response.getName()).isEqualTo(testRequest.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 생성 - 이메일 중복 실패")
    void createUser_EmailDuplicate() {
        // given
        when(userRepository.existsByEmail(testRequest.getEmail())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.createUser(testRequest))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.EMAIL_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("사용자 생성 - 잘못된 이메일 형식")
    void createUser_InvalidEmailFormat() {
        // given
        testRequest.setEmail("invalid-email");

        // when & then
        assertThatThrownBy(() -> userService.createUser(testRequest))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.INVALID_EMAIL_FORMAT);
    }

    @Test
    @DisplayName("사용자 조회 - 성공")
    void getUser_Success() {
        // given
        when(userRepository.findByUserId(testUserId)).thenReturn(Optional.of(testUser));

        // when
        UserResponse response = userService.getUser(testUserId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(testRequest.getEmail());
        assertThat(response.getName()).isEqualTo(testRequest.getName());
    }

    @Test
    @DisplayName("사용자 조회 - 존재하지 않는 사용자")
    void getUser_NotFound() {
        // given
        when(userRepository.findByUserId(testUserId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUser(testUserId))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("사용자 삭제 - 성공")
    void deleteUser_Success() {
        // given
        when(userRepository.findByUserId(testUserId)).thenReturn(Optional.of(testUser));

        // when
        userService.deleteUser(testUserId);

        // then
        verify(userRepository).delete(testUser);
    }

    @Test
    @DisplayName("사용자 삭제 - 존재하지 않는 사용자")
    void deleteUser_NotFound() {
        // given
        when(userRepository.findByUserId(testUserId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.deleteUser(testUserId))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.USER_NOT_FOUND);
    }
}
