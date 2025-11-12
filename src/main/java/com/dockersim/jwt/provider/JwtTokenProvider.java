package com.dockersim.jwt.provider;

import com.dockersim.config.SimulationUserPrincipal; // SimulationUserPrincipal 임포트
import com.dockersim.domain.User; // User 엔티티 임포트
import com.dockersim.exception.BusinessException; // BusinessException 임포트
import com.dockersim.exception.code.UserErrorCode; // UserErrorCode 임포트
import com.dockersim.jwt.dto.TokenInfo;
import com.dockersim.service.user.UserFinder; // UserFinder 임포트
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessTokenExpirationMillis;
    private final long refreshTokenExpirationMillis;
    private final UserFinder userFinder; // UserFinder 주입

    // 1. application-secret.yml에서 설정 값을 주입받는 생성자
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.access-token-expiration-millis}") long accessTokenExpirationMillis,
                            @Value("${jwt.refresh-token-expiration-millis}") long refreshTokenExpirationMillis,
                            UserFinder userFinder) { // UserFinder 주입
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpirationMillis = accessTokenExpirationMillis;
        this.refreshTokenExpirationMillis = refreshTokenExpirationMillis;
        this.userFinder = userFinder; // UserFinder 초기화
    }

    public String createAccessToken(String userId, List<String> roles) {
        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + accessTokenExpirationMillis);

        return Jwts.builder()
                .setSubject(userId)
                .claim("auth", String.join(",", roles))
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken() {
        long now = (new Date()).getTime();
        Date refreshTokenExpiresIn = new Date(now + refreshTokenExpirationMillis);

        return Jwts.builder()
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public TokenInfo generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        Date accessTokenExpiresIn = new Date(now + accessTokenExpirationMillis);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + refreshTokenExpirationMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        List<String> roles = Arrays.stream(claims.get("auth").toString().split(","))
                        .collect(Collectors.toList());

        String userPublicIdFromToken = claims.getSubject();
        log.info("JwtTokenProvider: userPublicId from token subject: {}", userPublicIdFromToken);

        User user = userFinder.findUserByPublicId(userPublicIdFromToken);
        log.info("JwtTokenProvider: User found by UserFinder: {}", user);
        if (user != null) {
            log.info("JwtTokenProvider: User's publicId from DB: {}", user.getPublicId());
        } else {
            log.error("JwtTokenProvider: User not found by userFinder for publicId: {}", userPublicIdFromToken);
            throw new BusinessException(UserErrorCode.USER_NOT_FOUND, userPublicIdFromToken);
        }

        // SimulationUserPrincipal 생성 시 roles 정보도 함께 전달
        SimulationUserPrincipal principal = new SimulationUserPrincipal(user, roles);

        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities()); // principal.getAuthorities() 사용
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
