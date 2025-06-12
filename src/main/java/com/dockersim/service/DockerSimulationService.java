package com.dockersim.service;

import com.dockersim.dto.ParsedDockerCommand;
import com.dockersim.dto.request.CommandExecuteRequest;
import com.dockersim.dto.response.*;
import com.dockersim.parser.DockerCommandParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * DockerSimulationService
 *
 * This service handles the simulation of Docker commands.
 * It parses the user's command, identifies the resource group (container,
 * image, etc.),
 * and delegates execution to the appropriate simulation service.
 * It also maintains simulation states.
 *
 * Key features:
 * - Handles commands with quotes and escape characters
 * - Provides success/failure responses with detailed execution state
 * - Logs errors during command execution
 */

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DockerSimulationService {

    private final DockerCommandParser dockerCommandParser;
    private final ContainerSimulationService containerService;
    private final ImageSimulationService imageService;
    private final VolumeSimulationService volumeService;
    private final NetworkSimulationService networkService;

    public CommandExecuteResponse executeCommand(CommandExecuteRequest request) {
        try {
            // Step 1. Parsing Command
            ParsedDockerCommand parsedCommand = dockerCommandParser.parse(request.getCommand());

            if (!parsedCommand.isValid()) {
                return createFailureResponse(request.getCommand(), parsedCommand.getErrorMessage(),
                        request.getSimulationId());
            }

            // Step 2. Execute Command
            CommandExecuteResponse response = executeDockerCommand(parsedCommand, request.getSimulationId());

            // Step 3. Create Response
            return CommandExecuteResponse.builder()
                    .command(request.getCommand())
                    .output(response.getOutput())
                    .success(response.getSuccess())
                    .simulationId(request.getSimulationId())
                    .executedAt(LocalDateTime.now())
                    .containers(containerService.getCurrentContainers(request.getSimulationId()))
                    .images(imageService.getCurrentImages(request.getSimulationId()))
                    .volumes(volumeService.getCurrentVolumes(request.getSimulationId()))
                    .networks(networkService.getCurrentNetworks(request.getSimulationId()))
                    .stateChanges(response.getStateChanges())
                    .hint(response.getHint())
                    .help(response.getHelp())
                    .build();

        } catch (Exception e) {
            log.error("명령어 실행 중 오류 발생: {}", e.getMessage(), e);
            return createFailureResponse(request.getCommand(), "명령어 실행 중 오류가 발생했습니다: " + e.getMessage(),
                    request.getSimulationId());
        }
    }

    private CommandExecuteResponse executeDockerCommand(ParsedDockerCommand command, String simulationId) {
        String group = command.getGroup();

        switch (group) {
            case "container":
                return containerService.executeCommand(command, simulationId);
            case "image":
                return imageService.executeCommand(command, simulationId);
            case "network":
                return createSimpleResponse("네트워크 명령어 '" + command.getSubCommand() + "'가 실행되었습니다. [시뮬레이션]");
            case "volume":
                return createSimpleResponse("볼륨 명령어 '" + command.getSubCommand() + "'가 실행되었습니다. [시뮬레이션]");
            default:
                return createFailureResponse(command.getCommand(),
                        "지원하지 않는 명령어 그룹입니다: " + group, simulationId);
        }
    }

    private CommandExecuteResponse createSimpleResponse(String output) {
        return CommandExecuteResponse.builder()
                .output(output)
                .success(true)
                .build();
    }

    private CommandExecuteResponse createFailureResponse(String command, String errorMessage, String simulationId) {
        return CommandExecuteResponse.builder()
                .command(command)
                .output("Error: " + errorMessage)
                .success(false)
                .simulationId(simulationId)
                .executedAt(LocalDateTime.now())
                .build();
    }

}