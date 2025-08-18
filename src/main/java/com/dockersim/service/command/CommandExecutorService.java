package com.dockersim.service.command;

import com.dockersim.dto.response.CommandResult;

public interface CommandExecutorService {

    /**
     * 전체 명령어 문자열을 받아 실행하고, 그 결과를 객체로 반환합니다.
     *
     * @param rawCommand 사용자가 입력한 전체 명령어 문자열 (예: "docker image pull nginx")
     * @return Callable이 반환한 DTO
     */
    CommandResult execute(String rawCommand);
}
