package com.dockersim.controller;

import com.dockersim.common.response.ApiResponse;
import com.dockersim.common.response.CommonResponseCode;
import com.dockersim.dto.request.CommandExecuteRequest;
import com.dockersim.dto.response.CommandExecuteResponse;
import com.dockersim.service.DockerSimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/docker")
@RequiredArgsConstructor
@Tag(name = "Docker Simulation", description = "Docker 명령어 시뮬레이션 API")
public class DockerController {

  private final DockerSimulationService dockerSimulationService;

  @PostMapping("/execute")
  @Operation(
      summary = "Docker 명령어 실행",
      description = "Docker 명령어를 시뮬레이션 환경에서 실행합니다. 실제 Docker와 동일한 명령어를 사용할 수 있습니다."
  )
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200", 
          description = "명령어 실행 성공",
          content = @Content(schema = @Schema(implementation = CommandExecuteResponse.class))
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "400", 
          description = "잘못된 명령어 형식"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "500", 
          description = "서버 내부 오류"
      )
  })
  public ResponseEntity<ApiResponse<CommandExecuteResponse>> executeCommand(
      @Parameter(description = "실행할 Docker 명령어", required = true)
      @RequestBody CommandExecuteRequest request) {
    try {
      CommandExecuteResponse response = dockerSimulationService.executeCommand(request);
      return ResponseEntity.ok(ApiResponse.success(CommonResponseCode.SUCCESS, response));
    } catch (Exception e) {
      return ResponseEntity.ok(ApiResponse.fail(CommonResponseCode.INTERNAL_ERROR));
    }
  }
}