package com.dockersim.controller;

import com.dockersim.common.ApiResponse;
import com.dockersim.dto.request.UserRequest;
import com.dockersim.dto.response.UserResponse;
import com.dockersim.service.user.UserServiceImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    /**
     * 새로운 사용자 생성을 진행합니다.
     *
     * @param request 사용자 생성 요청 정보
     * @return 생성된 사용자 정보
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
        @RequestBody UserRequest request) {
        log.info("사용자 생성 {}", request.toString());
        return ResponseEntity.ok(ApiResponse.success(userService.createUser(request)));
    }


    /**
     * 사용자 정보를 조회합니다.
     *
     * @param userId 조회할 사용자 UUID
     * @return 조회된 사용자 정보
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(
        /* @AuthenticationPrincipal */ UUID userId
    ) {
        log.info("사용자 조회 ID={}", userId);

        UserResponse userResponse = userService.getUser(userId);

        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }


    /**
     * 사용자를 삭제합니다.
     *
     * @param userId 삭제할 사용자 UUID
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(
        /* @AuthenticationPrincipal */ UUID userId
    ) {
        log.info("사용자 삭제 ID={}", userId);

        userService.deleteUser(userId);

        return ResponseEntity.ok(ApiResponse.success());
    }
}