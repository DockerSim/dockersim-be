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

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    // 필요한 의존성들을 주입받습니다.
    private final UserRepository userRepository;
    private final WebClient webClient;
    private final JwtTokenProvider jwtTokenProvider;

    // application-secret.yml에 저장된 GitHub Client ID와 Secret을 주입받습니다.
    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    public LoginResponse githubLogin(String code) {
        // 1. 코드를 이용해 GitHub AccessToken 받기
        AccessTokenResponse tokenResponse = getAccessToken(code);

        // 2. AccessToken을 이용해 GitHub 사용자 정보 받기
        GithubUserResponse userInfo = getUserInfo(tokenResponse.getAccessToken());

        // 3. GitHub ID로 우리 DB에서 회원 조회, 없으면 자동 회원가입
        User user = userRepository.findByGithubId(String.valueOf(userInfo.getId()))
                .orElseGet(() -> {
                    User newUser = User.fromGithub(userInfo);
                    return userRepository.save(newUser);
                });

        // 4. 우리 서비스의 JWT 토큰 생성
        // User 엔티티에 roles 필드가 있다고 가정. 없다면 기본 권한을 부여.
        List<String> roles = user.getRoles(); // 예: user.getRoles()가 List<String>을 반환한다고 가정
        if (roles == null || roles.isEmpty()) {
            roles = List.of("ROLE_USER"); // 기본 권한 부여
        }

        String serviceAccessToken = jwtTokenProvider.createAccessToken(user.getUserId().toString(), roles);
        String serviceRefreshToken = jwtTokenProvider.createRefreshToken();

        // 5. 이메일 정보가 없는 신규 사용자인지 확인
        boolean isAdditionalInfoRequired = (user.getEmail() == null);

        // 6. 최종 응답 DTO에 두 토큰을 모두 담아 반환
        return new LoginResponse(serviceAccessToken, serviceRefreshToken, isAdditionalInfoRequired);
    }

    /**
     * WebClient를 사용하여 GitHub에 AccessToken을 요청하는 메서드
     */
    private AccessTokenResponse getAccessToken(String code) {
        return webClient.post()
                .uri("https://github.com/login/oauth/access_token")
                .header(HttpHeaders.ACCEPT, "application/json")
                .body(BodyInserters.fromFormData("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("code", code))
                .retrieve() // 응답을 받아옴
                .bodyToMono(AccessTokenResponse.class) // 응답 본문을 DTO로 변환
                .block(); // 비동기 처리를 동기적으로 기다림
    }

    /**
     * WebClient를 사용하여 GitHub에 사용자 정보를 요청하는 메서드
     */
    private GithubUserResponse getUserInfo(String accessToken) {
        return webClient.get()
                .uri("https://api.github.com/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(GithubUserResponse.class)
                .block();
    }

}
