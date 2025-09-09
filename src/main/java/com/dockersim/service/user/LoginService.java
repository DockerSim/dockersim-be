package com.dockersim.service.user;

import com.dockersim.domain.User;
import com.dockersim.dto.response.AccessTokenResponse;
import com.dockersim.dto.response.GithubUserResponse;
import com.dockersim.dto.response.LoginResponse;
import com.dockersim.jwt.provider.JwtTokenProvider;
import com.dockersim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final UserRepository userRepository;
    private final WebClient webClient;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    public LoginResponse githubLogin(String code) {
        try {
            // 1. 코드를 이용해 GitHub AccessToken 받기
            log.info("Attempting to get GitHub access token with code: {}", code);
            AccessTokenResponse tokenResponse = getAccessToken(code);
            log.info("Successfully received GitHub access token.");

            // 2. AccessToken을 이용해 GitHub 사용자 정보 받기
            log.info("Attempting to get user info from GitHub.");
            GithubUserResponse userInfo = getUserInfo(tokenResponse.getAccessToken());
            log.info("Successfully received user info for GitHub ID: {}", userInfo.getId());

            // 3. GitHub ID로 우리 DB에서 회원 조회, 없으면 자동 회원가입
            User user = userRepository.findByGithubId(String.valueOf(userInfo.getId()))
                    .orElseGet(() -> {
                        log.info("User not found in DB. Creating new user for GitHub ID: {}", userInfo.getId());
                        User newUser = User.fromGithub(userInfo);
                        return userRepository.save(newUser);
                    });

            // 4. 우리 서비스의 JWT 토큰 생성
            List<String> roles = user.getRoles();
            if (roles == null || roles.isEmpty()) {
                roles = List.of("ROLE_USER");
            }

            String serviceAccessToken = jwtTokenProvider.createAccessToken(user.getUserId().toString(), roles);
            String serviceRefreshToken = jwtTokenProvider.createRefreshToken();
            log.info("Successfully created service JWT for user ID: {}", user.getUserId());

            // 5. 이메일 정보가 없는 신규 사용자인지 확인
            boolean isAdditionalInfoRequired = (user.getEmail() == null);

            // 6. 최종 응답 DTO에 두 토큰을 모두 담아 반환
            return new LoginResponse(serviceAccessToken, serviceRefreshToken, isAdditionalInfoRequired);

        } catch (WebClientResponseException ex) {
            // GitHub 통신 중 에러 발생 시
            log.error("Error during GitHub communication: Status {}, Body {}", ex.getRawStatusCode(), ex.getResponseBodyAsString(), ex);
            // 여기서 커스텀 예외를 던져서 500 대신 더 구체적인 에러 코드를 반환하게 할 수 있습니다.
            throw new RuntimeException("GitHub login failed.", ex);
        } catch (Exception ex) {
            // 그 외 모든 예외 (NullPointerException 등)
            log.error("An unexpected error occurred during GitHub login", ex);
            throw new RuntimeException("Internal server error during login.", ex);
        }
    }

    private AccessTokenResponse getAccessToken(String code) {
        return webClient.post()
                .uri("https://github.com/login/oauth/access_token")
                .header(HttpHeaders.ACCEPT, "application/json")
                .body(BodyInserters.fromFormData("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("code", code))
                .retrieve()
                .bodyToMono(AccessTokenResponse.class)
                .block();
    }

    private GithubUserResponse getUserInfo(String accessToken) {
        return webClient.get()
                .uri("https://api.github.com/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(GithubUserResponse.class)
                .block();
    }
}