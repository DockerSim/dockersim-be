package com.dockersim.command.subcommand.container;

import com.dockersim.command.subcommand.ContainerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerContainerResponse;
import com.dockersim.service.container.DockerContainerService;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "restart")
@RequiredArgsConstructor
public class ContainerRestart implements Callable<CommandResult> {

    private final DockerContainerService service;

    @CommandLine.ParentCommand
    private ContainerCommand parent;

    @CommandLine.Parameters(index = "0", description = "다시 시작할 Container의 이름 또는 ID")
    private String nameOrHexId;

    @Override
    public CommandResult call() {
        List<DockerContainerResponse> containers = service.restart(parent.getPrincipal(), nameOrHexId);
        return CommandResult.builder()
                .console(
                        containers.stream()
                                .flatMap(c -> c.getConsole().stream())
                                .toList()
                )
                .changedContainers(containers)
                .status(CommandResultStatus.UPDATE)
                .build();
    }
}
