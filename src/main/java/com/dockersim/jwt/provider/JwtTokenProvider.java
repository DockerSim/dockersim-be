package com.dockersim.jwt.provider;

import com.dockersim.jwt.dto.TokenInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
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

    public String createAccessToken(String userId, List<String> roles) {
        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + accessTokenExpirationMillis);

        return Jwts.builder()
                .setSubject(userId) // 토큰의 주체로 userId(UUID)를 사용
                .claim("auth", String.join(",", roles)) // 권한 정보 추가
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // RefreshToken을 생성하는 메서드
    public String createRefreshToken() {
        long now = (new Date()).getTime();
        Date refreshTokenExpiresIn = new Date(now + refreshTokenExpirationMillis);

        return Jwts.builder()
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    // 1. application-secret.yml에서 설정 값을 주입받는 생성자
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.access-token-expiration-millis}") long accessTokenExpirationMillis,
                            @Value("${jwt.refresh-token-expiration-millis}") long refreshTokenExpirationMillis) {
        // 2. 주입받은 secretKey를 Base64 디코딩하여 Key 객체로 변환
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes); // HMAC-SHA 알고리즘을 사용하는 키 생성
        this.accessTokenExpirationMillis = accessTokenExpirationMillis;
        this.refreshTokenExpirationMillis = refreshTokenExpirationMillis;
    }

    // 3. 앞으로 여기에 토큰 생성, 검증, 정보 추출 메소드를 추가할 것입니다.
    // 유저 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
    public TokenInfo generateToken(Authentication authentication) {
        // 1. 사용자의 권한 정보 가져오기 (e.g., "ROLE_USER,ROLE_ADMIN")
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // 2. Access Token 생성
        Date accessTokenExpiresIn = new Date(now + accessTokenExpirationMillis);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())       // "sub" (Subject) : 토큰의 주체 (사용자 식별자)
                .claim("auth", authorities)                 // "auth" (Custom Claim) : 사용자의 권한 정보
                .setExpiration(accessTokenExpiresIn)        // "exp" (Expiration Time) : 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256)    // 서명에 사용할 키와 알고리즘 설정
                .compact();

        // 3. Refresh Token 생성 (리프레시 토큰에는 별도의 사용자 정보 없이 만료 시간만 설정)
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + refreshTokenExpirationMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // 4. 생성된 토큰들을 TokenInfo DTO에 담아 반환
        return TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            // 파싱 과정에서 서명 검증, 만료 시간 확인 등이 자동으로 이루어짐
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


    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // 1. 토큰 복호화하여 클레임(Payload 정보) 추출
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 2. 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 3. UserDetails 객체를 만들어서 Authentication 객체로 반환
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰에서 클레임을 추출하는 보조 메소드
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰의 경우에도 클레임 정보는 필요할 수 있으므로 반환
            return e.getClaims();
        }
    }

}
