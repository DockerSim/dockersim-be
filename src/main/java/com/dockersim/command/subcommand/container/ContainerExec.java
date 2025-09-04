package com.dockersim.command.subcommand.container;

import com.dockersim.command.subcommand.ContainerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.service.container.DockerContainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.concurrent.Callable;


@CommandLine.Command(name = "exec")
@Component
@RequiredArgsConstructor
public class ContainerExec implements Callable<CommandResult> {

    private final DockerContainerService service;

    @CommandLine.ParentCommand
    private final ContainerCommand parent;

    @CommandLine.Option(names = {"-i", "--interactice"})
    private boolean interactice;

    @CommandLine.Option(names = {"-t", "--tty"})
    private boolean tty;

    @CommandLine.Parameters(index = "0", description = "실행할 Container의 이름 또는 ID")
    private String nameOrHexId;

    @CommandLine.Parameters(index = "1", description = "컨테이너에서 실행할 명령어")
    private String command;

    @Override
    public CommandResult call() throws Exception {
        return null;
    }
}
