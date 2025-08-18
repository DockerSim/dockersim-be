package com.dockersim.unit.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.dockersim.domain.User;
import com.dockersim.dto.request.UserRequest;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("User 도메인 단위 테스트")
class UserTest {

    @Test
    @DisplayName("UserRequest로부터 User 엔티티 생성")
    void fromUserRequest() {
        // given
        String testName = "Test User";
        String testEmail = "test@example.com";
        UserRequest request = new UserRequest();
        request.setName(testName);
        request.setEmail(testEmail);

        // when
        User user = User.fromUserRequest(request);

        // then
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo(testName);
        assertThat(user.getEmail()).isEqualTo(testEmail);
        assertThat(user.getUserId()).isNotNull();
        assertThat(user.getCreatedAt()).isNotNull()
            .isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("UUID는 자동으로 생성되어야 함")
    void userIdShouldBeGenerated() {
        // given
        UserRequest request = new UserRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");

        // when
        User user = User.fromUserRequest(request);

        // then
        assertThat(user.getUserId())
            .isNotNull()
            .isInstanceOf(UUID.class);
    }

    @Test
    @DisplayName("Builder를 통한 User 생성")
    void createUserWithBuilder() {
        // given
        String testName = "Test User";
        String testEmail = "test@example.com";
        UUID testUserId = UUID.randomUUID();
        LocalDateTime testCreatedAt = LocalDateTime.now();

        // when
        User user = User.builder()
            .userId(testUserId)
            .name(testName)
            .email(testEmail)
            .createdAt(testCreatedAt)
            .build();

        // then
        assertThat(user.getName()).isEqualTo(testName);
        assertThat(user.getEmail()).isEqualTo(testEmail);
        assertThat(user.getUserId()).isEqualTo(testUserId);
        assertThat(user.getCreatedAt()).isEqualTo(testCreatedAt);
    }
}
