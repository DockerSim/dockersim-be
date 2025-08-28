package com.dockersim.command.subcommand.volume;

import com.dockersim.command.subcommand.VolumeCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.service.volume.DockerVolumeService;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;


@Component
@Command(name = "ls", aliases = "list")
@RequiredArgsConstructor
public class VolumeLsCommand implements Callable<CommandResult> {

    private final DockerVolumeService service;

    @ParentCommand
    private VolumeCommand parent;

    @Option(names = {"-q", "--quiet"}, description = "Docker Volume 이름만 출력")
    private boolean quiet;

    @Override
    public CommandResult call() throws Exception {
        return CommandResult.builder()
            .console(service.ls(parent.getPrincipal(), quiet))
            .status(CommandResultStatus.READ)
            .build();
    }
}