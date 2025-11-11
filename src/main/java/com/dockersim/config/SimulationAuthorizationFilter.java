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
import lombok.extern.slf4j.Slf4j; // Slf4j 임포트
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
@Slf4j // Slf4j 어노테이션 추가
public class SimulationAuthorizationFilter extends OncePerRequestFilter {

    private final UserFinder userFinder;
    private final SimulationFinder simulationFinder;
    private final SimulationCollaboratorRepository collaboratorRepository;
    private final AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher(
            "/api/simulations/{simulationPublicId}/**");
    private final AntPathRequestMatcher mySimulationsMatcher = new AntPathRequestMatcher(
            "/api/simulations/me", HttpMethod.GET.name()); // /me 엔드포인트 추가
    private final AntPathRequestMatcher composeGenerateMatcher = new AntPathRequestMatcher(
            "/api/simulations/{simulationPublicId}/compose", HttpMethod.POST.name()); // compose 생성 엔드포인트 추가

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 사용자 정보만 담긴 인증정보가 있어야함.
        // /api/simulations/me 요청은 시뮬레이션 ID 인증이 불필요
        if (authentication == null || (!requestMatcher.matches(request) && !mySimulationsMatcher.matches(request))) {
            log.debug("User info is missing or simulation ID authentication is not required for request: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        // /api/simulations/me 요청은 시뮬레이션 ID 검증 로직을 건너뜀
        if (mySimulationsMatcher.matches(request)) {
            log.debug("Skipping simulation ID validation for /api/simulations/me request.");
            filterChain.doFilter(request, response);
            return;
        }

        // 이미 시뮬레이션 정보까지 검증된 인증정보면 인증절차 생략
        if (authentication instanceof SimulationAuthenticationToken) {
            log.debug("Authentication already contains simulation info. Skipping further validation.");
            filterChain.doFilter(request, response);
            return;
        }

        // 유효한 사용자, 시뮬레이션인지 검증해야함.
        String userPublicId = authentication.getName();
        String simulationPublicId = requestMatcher.matcher(request).getVariables()
                .get("simulationPublicId");

        log.debug("Authenticating user: {} for simulation: {}", userPublicId, simulationPublicId);

        User user = userFinder.findUserByPublicId(userPublicId);
        Simulation simulation = simulationFinder.findByPublicId(simulationPublicId);

        if (!hasPermission(request, user, simulation)) {
            log.warn("Access denied for user: {} to simulation: {}", userPublicId, simulationPublicId);
            throw new AccessDeniedException("이 시뮬레이션에 접근할 권한이 없습니다.");
        }

        // 시뮬레이션 정보가 담긴 새로운 인증정보를 컨택스트에 저장해야함
        SecurityContextHolder.getContext().setAuthentication(
                new SimulationAuthenticationToken(new SimulationUserPrincipal(user, simulation, user.getRoles()))
        );

        filterChain.doFilter(request, response);
    }


    private boolean hasPermission(HttpServletRequest request, User user, Simulation simulation) {
        // 1. 소유자는 모든 권한 허용
        if (simulation.getOwner().getId().equals(user.getId())) {
            log.debug("Permission granted: User is owner of simulation.");
            return true;
        }

        // 2. PRIVATE는 소유자만 접근 가능 (이미 위에서 소유자 체크했으므로, 소유자가 아니면 접근 불가)
        SimulationShareState shareState = simulation.getShareState();
        if (shareState == SimulationShareState.PRIVATE) {
            log.debug("Permission denied: Simulation is PRIVATE and user is not owner.");
            return false;
        }

        // 3. READ이면 누구든 읽기 요청만 가능
        if (shareState == SimulationShareState.READ) {
            if (isReadOperation(request)) {
                log.debug("Permission granted: Simulation is READ and request is GET.");
                return true;
            }
            // compose 파일 생성은 POST 요청이므로 READ 상태에서는 허용하지 않음
            if (composeGenerateMatcher.matches(request)) {
                log.debug("Permission denied: Simulation is READ, but request is POST to generate compose.");
                return false;
            }
            log.debug("Permission denied: Simulation is READ, but request is not GET.");
            return false;
        }

        // 4. WRITE이고, 협업자면 접근 가능
        if (shareState == SimulationShareState.WRITE) {
            if (collaboratorRepository.existsBySimulationAndUser(simulation, user)) {
                log.debug("Permission granted: Simulation is WRITE and user is collaborator.");
                return true;
            }
            log.debug("Permission denied: Simulation is WRITE, but user is not collaborator.");
            return false;
        }
        log.debug("Permission denied: No matching permission rule for share state: {}", shareState);
        return false;
    }

    private boolean isReadOperation(HttpServletRequest request) {
        return HttpMethod.GET.name().equalsIgnoreCase(request.getMethod());
    }
}
