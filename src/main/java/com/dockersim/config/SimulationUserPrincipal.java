package com.dockersim.config;


import com.dockersim.domain.Simulation;
import com.dockersim.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SimulationUserPrincipal implements UserDetails { // UserDetails 인터페이스 구현

    private final Long userId;
    private final String userPublicId;
    private final Long simulationId;
    private final String simulationPublicId;
    private final Collection<? extends GrantedAuthority> authorities; // 권한 정보 추가

    // User와 Simulation 정보를 모두 받는 생성자
    public SimulationUserPrincipal(User user, Simulation simulation, List<String> roles) {
        this.userId = user.getId();
        this.userPublicId = user.getPublicId();
        this.simulationId = simulation.getId();
        this.simulationPublicId = simulation.getPublicId();
        this.authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    // User 정보만 받는 생성자 (Simulation이 없는 경우 사용)
    public SimulationUserPrincipal(User user, List<String> roles) {
        this.userId = user.getId();
        this.userPublicId = user.getPublicId();
        this.simulationId = null;
        this.simulationPublicId = null;
        this.authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    // UserDetails 인터페이스 구현 메서드들
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // JWT 기반 인증이므로 비밀번호는 필요 없음
    }

    @Override
    public String getUsername() {
        return this.userPublicId; // 사용자 식별자로 userPublicId 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
