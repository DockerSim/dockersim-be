package com.dockersim.command.subcommand.container;

import com.dockersim.command.subcommand.ContainerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerContainerResponse;
import com.dockersim.service.container.DockerContainerService;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "unpause")
@RequiredArgsConstructor
public class ContainerUnpause implements Callable<CommandResult> {

    private final DockerContainerService service;

    @CommandLine.ParentCommand
    private ContainerCommand parent;

    @CommandLine.Parameters(index = "0", description = "일시정지 상태를 해제할 Container 이름 또는 Hex Id")
    private String nameOrHexId;

    @Override
    public CommandResult call() throws Exception {
        DockerContainerResponse unpauseContainer = service.unpause(parent.getPrincipal(), nameOrHexId);
        return CommandResult.builder()
                .console(unpauseContainer.getConsole())
                .changedContainer(unpauseContainer)
                .status(CommandResultStatus.UPDATE)
                .build();
    }
}
