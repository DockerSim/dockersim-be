package com.dockersim.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Docker 명령어 실행 결과")
public class CommandExecuteResponse {

  @Schema(description = "실행된 명령어", example = "docker ps")
  private String command;

  @Schema(description = "명령어 실행 결과 출력", example = "CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES")
  private String output;

  @Schema(description = "실행 성공 여부", example = "true")
  private Boolean success;

  @Schema(description = "시뮬레이션 ID", example = "sim_123456")
  private String simulationId;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(description = "실행 시간", example = "2024-01-15 14:30:00")
  private LocalDateTime executedAt;

  // 시각화용 데이터
  @Schema(description = "컨테이너 목록")
  private List<ContainerSimulationDto> containers;

  @Schema(description = "이미지 목록")
  private List<ImageSimulationDto> images;

  @Schema(description = "네트워크 목록")
  private List<NetworkSimulationDto> networks;

  @Schema(description = "볼륨 목록")
  private List<VolumeSimulationDto> volumes;

  // 상태 변화 정보
  @Schema(description = "상태 변화 정보")
  private Map<String, Object> stateChanges;

  // 힌트 및 도움말
  @Schema(description = "학습 힌트", example = "docker ps 명령어는 실행 중인 컨테이너를 확인할 때 사용합니다.")
  private String hint;

  @Schema(description = "도움말", example = "옵션: -a (모든 컨테이너), -q (ID만 표시)")
  private String help;

  public static CommandExecuteResponse success(String command, String output) {
    return CommandExecuteResponse.builder()
        .command(command)
        .output(output)
        .success(true)
        .executedAt(LocalDateTime.now())
        .build();
  }

  public static CommandExecuteResponse failure(String command, String output) {
    return CommandExecuteResponse.builder()
        .command(command)
        .output(output)
        .success(false)
        .executedAt(LocalDateTime.now())
        .build();
  }
}