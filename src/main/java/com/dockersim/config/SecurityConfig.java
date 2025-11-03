package com.dockersim.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dockersim.config.auth.CustomAccessDeniedHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	// Profile에 따라 DevUserInjectionFilter 또는 JwtAuthenticationFilter가 주입됨
	@Qualifier("authenticationFilter")
	private final OncePerRequestFilter authenticationFilter;

	// 시뮬레이션 소유권 인가 필터
	private final SimulationAuthorizationFilter simulationAuthorizationFilter;

	// 보안 예외를 처리할 커스텀 핸들러
	private final CustomAccessDeniedHandler customAccessDeniedHandler;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)

			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/api/public/**",
					"/swagger-ui/**", // Swagger UI 경로
					"/v3/api-docs/**"  // API 문서 경로
				).permitAll()
				.requestMatchers(HttpMethod.POST, "/api/users").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
				.requestMatchers("/hc").permitAll()
				.requestMatchers("/api/officeimage/**").permitAll()
				.anyRequest().authenticated()
			)
			.exceptionHandling(exceptions ->
				exceptions.accessDeniedHandler(customAccessDeniedHandler)
			)
			.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterAfter(simulationAuthorizationFilter, authenticationFilter.getClass())
			.build();
	}
}
