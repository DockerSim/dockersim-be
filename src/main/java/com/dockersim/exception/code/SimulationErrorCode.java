package com.dockersim.exception.code;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SimulationErrorCode implements ResponseCode {
    SIMULATION_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "시뮬레이션을 찾을 수 없습니다"),
    SIMULATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "S002", "시뮬레이션에 대한 권한이 없습니다"),
    SIMULATION_INVALID_SHARE_STATE(HttpStatus.BAD_REQUEST, "S003", "올바르지 않은 공유 상태입니다"),
    SIMULATION_TITLE_DUPLICATE(HttpStatus.BAD_REQUEST, "S0041", "이미 존재하는 시뮬레이션 제목입니다"),
    SIMULATION_TITLE_NOT_INVALID(HttpStatus.BAD_REQUEST, "S0042", "유효하지 않는 시뮬레이션 제목입니다 %s"),
    SIMULATION_OWNER_CANNOT_BE_COLLABORATOR(
        HttpStatus.BAD_REQUEST, "S005", "시뮬레이션 소유자는 협업자가 될 수 없습니다"),
    SIMULATION_COLLABORATOR_ALREADY_EXISTS(
        HttpStatus.BAD_REQUEST, "S006", "이미 협업자로 등록된 사용자입니다"),
    SIMULATION_COLLABORATOR_NOT_FOUND(HttpStatus.NOT_FOUND, "S007", "협업자를 찾을 수 없습니다"),
    SIMULATION_INVALID_PERMISSION(HttpStatus.BAD_REQUEST, "S008", "올바르지 않은 권한입니다"),
    SIMULATION_ONLY_OWNER_CAN_MANAGE(HttpStatus.FORBIDDEN, "S009", "시뮬레이션 소유자만 관리할 수 있습니다");

    private final HttpStatus status;
    private final String code;
    private final String template;

}
