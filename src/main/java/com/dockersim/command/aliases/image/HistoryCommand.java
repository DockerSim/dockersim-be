package com.dockersim.command.aliases.image;

import com.dockersim.command.DockerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.service.image.DockerImageService;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;


@Command(name = "history")
@Component
@RequiredArgsConstructor
public class HistoryCommand implements Callable<CommandResult> {

    private final DockerImageService service;

    @ParentCommand
    private DockerCommand parent;

    @CommandLine.Parameters(index = "0", description = "Docker Image 이름 또는 Hex ID")
    private String nameOrHexId;

    @Override
    public CommandResult call() throws Exception {
        return CommandResult.builder()
            .console(service.history(parent.getPrincipal(), nameOrHexId))
            .status(CommandResultStatus.READ)
            .build();
    }
}