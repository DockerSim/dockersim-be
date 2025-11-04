package com.dockersim.command.aliases.container;

import com.dockersim.command.DockerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerContainerResponse;
import com.dockersim.service.container.DockerContainerService;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "start")
@RequiredArgsConstructor
public class Start implements Callable<CommandResult> {

    private final DockerContainerService service;

    @CommandLine.ParentCommand
    private DockerCommand parent;

    @CommandLine.Parameters(index = "0", description = "실행할 Container 이름 또는 Hex Id")
    private String nameOrHexId;

    @Override
    public CommandResult call() throws Exception {
        DockerContainerResponse startContainer = service.start(parent.getPrincipal(), nameOrHexId);
        return CommandResult.builder()
                .console(startContainer.getConsole())
                .changedContainer(startContainer)
                .status(CommandResultStatus.UPDATE)
                .build();
    }
}
