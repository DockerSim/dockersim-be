package com.dockersim.command.subcommand.container;

import com.dockersim.command.subcommand.ContainerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.service.container.DockerContainerService;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "inspect")
@RequiredArgsConstructor
public class ContainerInspect implements Callable<CommandResult> {

    private final DockerContainerService service;

    @CommandLine.ParentCommand
    private ContainerCommand parent;

    @CommandLine.Parameters(index = "0", description = "상세 정보를 출력할 Container의 이름 또는 ID")
    private String nameOrHexId;

    @Override
    public CommandResult call() throws Exception {
        List<String> console = service.inspect(parent.getPrincipal(), nameOrHexId);
        return CommandResult.builder()
                .console(console)
                .status(CommandResultStatus.READ)
                .build();
    }
}
