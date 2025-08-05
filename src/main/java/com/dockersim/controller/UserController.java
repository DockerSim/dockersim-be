package com.dockersim.controller;

import com.dockersim.dto.request.CreateUserRequest;
import com.dockersim.dto.response.UserResponse;
import com.dockersim.service.UserService;
import com.dockersim.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 실무 표준: 사용자 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 사용자 생성 API
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody CreateUserRequest request) {
        log.info("사용자 생성 API 호출: {}", request.getEmail());

        UserResponse userResponse = userService.createUser(request);
        ApiResponse<UserResponse> response = ApiResponse.ok(userResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 사용자 조회 API
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        log.info("사용자 조회 API 호출: ID={}", id);

        UserResponse userResponse = userService.getUser(id);
        ApiResponse<UserResponse> response = ApiResponse.ok(userResponse);

        return ResponseEntity.ok(response);
    }
}