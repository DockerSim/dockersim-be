package com.dockersim.service;

import com.dockersim.dto.ParsedCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DockerCommandExecutor {
    public String executeCommand(ParsedCommand command) {
        String type = command.getCommand();
        return type;
    }
}
