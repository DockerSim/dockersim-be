package com.dockersim.command.subcommand.volume;

import com.dockersim.command.subcommand.VolumeCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.service.volume.DockerVolumeService;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;


@Component
@Command(name = "inspect")
@RequiredArgsConstructor
public class VolumeInspectCommand implements Callable<CommandResult> {

    private final DockerVolumeService service;

    @ParentCommand
    private VolumeCommand parent;

    @CommandLine.Parameters(index = "0", description = "조회할 Docker Volume 이름")
    private String nameOrHexId;

    @Override
    public CommandResult call() throws Exception {
        return CommandResult.builder()
            .console(service.inspect(parent.getPrincipal(), nameOrHexId))
            .status(CommandResultStatus.READ)
            .build();
    }
}
