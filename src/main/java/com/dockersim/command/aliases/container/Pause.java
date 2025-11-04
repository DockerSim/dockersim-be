package com.dockersim.command.aliases.container;

import com.dockersim.command.DockerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerContainerResponse;
import com.dockersim.service.container.DockerContainerService;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "pause")
@RequiredArgsConstructor
public class Pause implements Callable<CommandResult> {

    private final DockerContainerService service;

    @CommandLine.ParentCommand
    private DockerCommand parent;

    @CommandLine.Parameters(index = "0", description = "일시 정지할 Container의 이름 또는 ID")
    private String nameOrHexId;

    @Override
    public CommandResult call() throws Exception {
        DockerContainerResponse pausedContainer = service.pause(parent.getPrincipal(), nameOrHexId);
        return CommandResult.builder()
                .console(pausedContainer.getConsole())
                .changedContainer(pausedContainer)
                .status(CommandResultStatus.UPDATE)
                .build();
    }
}
