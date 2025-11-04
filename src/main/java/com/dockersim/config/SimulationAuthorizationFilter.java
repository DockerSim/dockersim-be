package com.dockersim.config;

import com.dockersim.config.auth.SimulationAuthenticationToken;
import com.dockersim.domain.Simulation;
import com.dockersim.domain.SimulationShareState;
import com.dockersim.domain.User;
import com.dockersim.repository.SimulationCollaboratorRepository;
import com.dockersim.service.simulation.SimulationFinder;
import com.dockersim.service.user.UserFinder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;


@Component
@RequiredArgsConstructor
public class SimulationAuthorizationFilter extends OncePerRequestFilter {

    private final UserFinder userFinder;
    private final SimulationFinder simulationFinder;
    private final SimulationCollaboratorRepository collaboratorRepository;
    private final AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher(
            "/api/simulations/{simulationPublicId}/**");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 사용자 정보만 담긴 인증정보가 있어야함.
        if (authentication == null || !requestMatcher.matches(request)) {
            System.out.println("사용자 정보가 없거나, 시뮬레이션 ID인증이 불필요");
            filterChain.doFilter(request, response);
            return;
        }

        // 이미 시뮬레이션 정보까지 검증된 인증정보면 인증절차 생략
        if (authentication instanceof SimulationAuthenticationToken) {
            filterChain.doFilter(request, response);
            return;
        }

        // 유효한 사용자, 시뮬레이션인지 검증해야함.
        String userPublicId = authentication.getName();
        String simulationPublicId = requestMatcher.matcher(request).getVariables()
                .get("simulationPublicId");

        System.out.println(userPublicId);

        User user = userFinder.findUserByPublicId(userPublicId);
        Simulation simulation = simulationFinder.findByPublicId(simulationPublicId);

        if (!hasPermission(request, user, simulation)) {
            throw new AccessDeniedException("이 시뮬레이션에 접근할 권한이 없습니다.");
        }

        // 시뮬레이션 정보가 담긴 새로운 인증정보를 컨택스트에 저장해야함
        SecurityContextHolder.getContext().setAuthentication(
                new SimulationAuthenticationToken(new SimulationUserPrincipal(user, simulation))
        );

        filterChain.doFilter(request, response);
    }


    private boolean hasPermission(HttpServletRequest request, User user, Simulation simulation) {
        // 1. 소유자는 모든 권한 허용
        if (simulation.getOwner().getId().equals(user.getId())) {
            return true;
        }

        // 2. PRIVATE는 소유자만 접근 가능
        SimulationShareState shareState = simulation.getShareState();
        if (shareState == SimulationShareState.PRIVATE) {
            return false;
        }

        // 3. READ이면 누구든 읽기 요청만 가능
        if (shareState == SimulationShareState.READ) {
            return isReadOperation(request);
        }

        // 4. WRITE이고, 협업자면 접근 가능
        if (shareState == SimulationShareState.WRITE) {
            return collaboratorRepository.existsBySimulationAndUser(simulation, user);
        }
        return false;
    }

    private boolean isReadOperation(HttpServletRequest request) {
        return HttpMethod.GET.name().equalsIgnoreCase(request.getMethod());
    }
}
