package com.dockersim.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@Tag(name = "서버 상태 확인 API", description = "서비스 상태 확인 API")
public class HealthController {

    /**
     * 서비스 상태를 확인합니다.
     *
     * @return 서비스 상태 확인 응답
     */
    @GetMapping("/hc")
    @Operation(summary = "서비스 상태 확인", description = "Docker 시뮬레이터 서비스의 상태를 확인합니다.")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Docker Simulator is running!");
    }
}