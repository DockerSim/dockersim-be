package com.dockersim.service.command;

import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerCommandErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.*;

class CommandExecutorServiceImplTest {

    @Test
    @DisplayName("null 입력 시 INVALID_DOCKER_COMMAND 예외")
    void execute_nullCommand_throwsBusinessException() {
        // given: dependencies are not needed because method exits early for invalid input
        CommandLine.IFactory factory = null;
        com.dockersim.parser.DockerCommandParser parser = null;
        CommandExecutorServiceImpl service = new CommandExecutorServiceImpl(factory, parser);

        // when + then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.execute(null, null));
        assertEquals(DockerCommandErrorCode.INVALID_DOCKER_COMMAND, ex.getErrorCode());
    }

    @Test
    @DisplayName("docker로 시작하지 않는 입력 시 INVALID_DOCKER_COMMAND 예외")
    void execute_nonDockerCommand_throwsBusinessException() {
        // given
        CommandLine.IFactory factory = null;
        com.dockersim.parser.DockerCommandParser parser = null;
        CommandExecutorServiceImpl service = new CommandExecutorServiceImpl(factory, parser);

        // when + then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.execute("echo hello", null));
        assertEquals(DockerCommandErrorCode.INVALID_DOCKER_COMMAND, ex.getErrorCode());
    }
}
