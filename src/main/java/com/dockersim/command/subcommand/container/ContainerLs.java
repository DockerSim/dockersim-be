package com.dockersim.command.subcommand.container;

import com.dockersim.command.subcommand.ContainerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.service.container.DockerContainerService;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "ls", aliases = {"list", "ps"})
@RequiredArgsConstructor
public class ContainerLs implements Callable<CommandResult> {

    private final DockerContainerService service;

    @CommandLine.ParentCommand
    private ContainerCommand parent;

    @CommandLine.Option(names = {"-a", "--all"}, description = "Existed 상태의 Container 출력 여부")
    private boolean all = false;

    @CommandLine.Option(names = {"-q", "--quiet"}, description = "Container ID만 출력 여부")
    private boolean quiet = false;

    @Override
    public CommandResult call() throws Exception {
        List<String> console = service.ps(parent.getPrincipal(), all, quiet);
        return CommandResult.builder()
                .console(console)
                .status(CommandResultStatus.READ)
                .build();
    }
}