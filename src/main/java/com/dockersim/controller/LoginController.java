package com.dockersim.controller;

import com.dockersim.dto.response.LoginResponse;
import com.dockersim.service.user.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin; // CrossOrigin 임포트
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod; // RequestMethod 임포트

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}) // <-- 이 줄 추가
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/api/login/github") // <-- 이 줄을 변경
    public ResponseEntity<LoginResponse> githubLogin(@RequestParam String code) {
        log.info("Received GitHub callback with code: {}", code);
        LoginResponse loginResponse = loginService.githubLogin(code);
        return ResponseEntity.ok(loginResponse);
    }

}
