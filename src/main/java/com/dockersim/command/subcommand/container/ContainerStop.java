package com.dockersim.command.subcommand.container;

import com.dockersim.command.subcommand.ContainerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerContainerResponse;
import com.dockersim.service.container.DockerContainerService;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "stop")
@RequiredArgsConstructor
public class ContainerStop implements Callable<CommandResult> {

    private final DockerContainerService service;

    @CommandLine.ParentCommand
    private ContainerCommand parent;

    @CommandLine.Parameters(index = "0", description = "중지할 Container의 이름 또는 Hex ID")
    private String nameOrHexId;

    @Override
    public CommandResult call() throws Exception {
        DockerContainerResponse stopContainer = service.stop(parent.getPrincipal(), nameOrHexId);
        return CommandResult.builder()
                .console(stopContainer.getConsole())
                .changedContainer(stopContainer)
                .status(CommandResultStatus.UPDATE)
                .build();
    }
}
