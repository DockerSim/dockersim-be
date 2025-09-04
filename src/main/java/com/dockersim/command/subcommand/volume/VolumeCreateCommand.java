package com.dockersim.command.subcommand.volume;

import com.dockersim.command.subcommand.VolumeCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerVolumeResponse;
import com.dockersim.service.volume.DockerVolumeService;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;


@Component
@Command(name = "create")
@RequiredArgsConstructor
public class VolumeCreateCommand implements Callable<CommandResult> {

    private final DockerVolumeService service;

    @ParentCommand
    private VolumeCommand parent;

    @CommandLine.Parameters(index = "0", description = "새로 생성하는 Docker Volume 이름")
    private String name;

    @Override
    public CommandResult call() throws Exception {
        DockerVolumeResponse volume = service.create(parent.getPrincipal(), name, false);
        return CommandResult.builder()
            .console(volume.getConsole())
            .status(CommandResultStatus.CREATE)
            .changedVolume(volume)
            .build();
    }
}
