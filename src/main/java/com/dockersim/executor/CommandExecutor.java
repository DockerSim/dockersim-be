package com.dockersim.executor;

import com.dockersim.parser.ParsedDockerCommand;

/**
 * Docker 명령어 실행 엔진 인터페이스
 */
public interface CommandExecutor {

    /**
     * 파싱된 Docker 명령어를 실행
     * 
     * @param parsedCommand 파싱된 명령어
     * @param simulationId  시뮬레이션 ID
     * @return 실행 결과
     */
    CommandExecuteResult execute(ParsedDockerCommand parsedCommand, Long simulationId);

    /**
     * 특정 명령어가 실행 가능한지 확인
     * 
     * @param command 확인할 명령어
     * @return 실행 가능 여부
     */
    boolean canExecute(String command);
}