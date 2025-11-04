package com.dockersim.command.subcommand.container;

import com.dockersim.command.subcommand.ContainerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerContainerResponse;
import com.dockersim.service.container.DockerContainerService;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "remove")
@RequiredArgsConstructor
public class ContainerRm implements Callable<CommandResult> {

    private final DockerContainerService service;

    @CommandLine.ParentCommand
    private ContainerCommand parent;

    @CommandLine.Option(names = {"-f", "--force"}, description = "Running 상태가 아닌 Container의 삭제 여부")
    private boolean force;

    @CommandLine.Parameters(index = "0", description = "삭제할 Container 이름 또는 ID")
    private String nameOrHexId;

    @Override
    public CommandResult call() throws Exception {
        DockerContainerResponse removeContainer = service.rm(parent.getPrincipal(), nameOrHexId);
        return CommandResult.builder()
                .console(removeContainer.getConsole())
                .changedContainer(removeContainer)
                .status(CommandResultStatus.DELETE)
                .build();
    }
}
