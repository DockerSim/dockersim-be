package com.dockersim.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Health Check API", description = "서비스 상태 확인 API")
@CrossOrigin(origins = "*")
public class HealthController {

    @GetMapping("/hc")
    @Operation(summary = "서비스 상태 확인", description = "Docker 시뮬레이터 서비스의 상태를 확인합니다.")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Docker Simulator is running!");
    }
}