package com.dockersim.controller;

import com.dockersim.common.ApiResponse;
import com.dockersim.dto.request.UserRequest;
import com.dockersim.dto.response.UserResponse;
import com.dockersim.service.user.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "사용자 API", description = "사용자 생성, 조회, 삭제 관리 API")
public class UserController {

    private final UserServiceImpl userService;

    /**
     * 새로운 사용자 생성을 진행합니다.
     *
     * @param request 사용자 생성 요청 정보
     * @return 생성된 사용자 정보
     */
    @Operation(summary = "사용자 생성", description = "사용자를 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
        @Parameter(description = "사용자 생성 요청 정보") @RequestBody UserRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.createUser(request)));
    }


    /**
     * 사용자 정보를 조회합니다.
     *
     * @param userId 조회할 사용자 UUID
     * @return 조회된 사용자 정보
     */
    @Operation(summary = "사용자 정보 조회", description = "사용자 정보를 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(
        @Parameter(hidden = true, description = "조회할 사용자 UUID") /* @AuthenticationPrincipal */ UUID userId
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUser(userId)));
    }


    /**
     * 사용자를 삭제합니다.
     *
     * @param userId 삭제할 사용자 UUID
     */
    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(
        @Parameter(hidden = true, description = "삭제할 사용자 UUID") /* @AuthenticationPrincipal */ UUID userId
    ) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}