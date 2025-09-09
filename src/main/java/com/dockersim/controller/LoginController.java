package com.dockersim.controller;

import com.dockersim.dto.response.LoginResponse;
import com.dockersim.service.user.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/api/auth/github/callback")
    public ResponseEntity<LoginResponse> githubLogin(@RequestParam String code) {
        log.info("Received GitHub callback with code: {}", code);
        LoginResponse loginResponse = loginService.githubLogin(code);
        return ResponseEntity.ok(loginResponse);
    }

}
