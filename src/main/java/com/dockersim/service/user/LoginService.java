package com.dockersim.service.user;

import com.dockersim.domain.User;
import com.dockersim.dto.response.AccessTokenResponse;
import com.dockersim.dto.response.GithubUserResponse;
import com.dockersim.dto.response.LoginResponse;
import com.dockersim.dto.response.UserResponse;
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
            log.info("Attempting to get GitHub access token with code: {}", code);
            AccessTokenResponse tokenResponse = getAccessToken(code);
            log.info("Successfully received GitHub access token.");

            log.info("Attempting to get user info from GitHub.");
            GithubUserResponse userInfo = getUserInfo(tokenResponse.getAccessToken());
            log.info("Successfully received user info for GitHub ID: {}", userInfo.getId());

            User user = userRepository.findByGithubId(String.valueOf(userInfo.getId()))
                    .orElseGet(() -> {
                        log.info("User not found in DB. Creating new user for GitHub ID: {}", userInfo.getId());
                        User newUser = User.fromGithub(userInfo);
                        log.info("New User publicId BEFORE save: {}", newUser.getPublicId());
                        log.info("New User roles BEFORE save: {}", newUser.getRoles()); // 로그 추가
                        User savedUser = userRepository.save(newUser);
                        log.info("New User publicId AFTER save: {}", savedUser.getPublicId());
                        log.info("New User roles AFTER save: {}", savedUser.getRoles()); // 로그 추가
                        return savedUser;
                    });
            
            if (user.getId() != null) { // 이미 존재하는 사용자라면
                log.info("Existing user found with publicId: {}", user.getPublicId());
                log.info("Existing user roles: {}", user.getRoles()); // 로그 추가
            }


            List<String> roles = user.getRoles();
            if (roles == null || roles.isEmpty()) {
                roles = List.of("ROLE_USER");
            }

            String serviceAccessToken = jwtTokenProvider.createAccessToken(user.getPublicId(), roles);
            String serviceRefreshToken = jwtTokenProvider.createRefreshToken();
            log.info("Successfully created service JWT for user ID: {}", user.getPublicId());

            boolean isAdditionalInfoRequired = (user.getEmail() == null);

            UserResponse userResponse = UserResponse.from(user);
            log.info("Generated UserResponse: {}", userResponse);
            log.info("UserResponse Public ID: {}", userResponse.getUserPublicId());

            return new LoginResponse(serviceAccessToken, serviceRefreshToken, isAdditionalInfoRequired, userResponse);

        } catch (WebClientResponseException ex) {
            log.error("Error during GitHub communication: Status {}, Body {}", ex.getRawStatusCode(), ex.getResponseBodyAsString(), ex);
            throw new RuntimeException("GitHub login failed.", ex);
        } catch (Exception ex) {
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
