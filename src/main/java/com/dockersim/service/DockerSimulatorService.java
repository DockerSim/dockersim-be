package com.dockersim.service;

import com.dockersim.dto.ParsedCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DockerSimulatorService {
    private final DockerCommandParser parser;
    private final DockerCommandExecutor executor;

    public String simulate(String rawCommand) {
        ParsedCommand parsed = parser.parseCommand(rawCommand);
        if (parsed == null) {
            return null;
        }
        return executor.executeCommand(parsed);
    }
}
