package com.dockersim.command.aliases.image;

import com.dockersim.command.DockerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.service.image.DockerImageService;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;


@Command(name = "push")
@Component
@RequiredArgsConstructor
public class PushCommand implements Callable<CommandResult> {

    private final DockerImageService service;

    @ParentCommand
    private DockerCommand parent;

    @CommandLine.Parameters(index = "0", description = "Docker Image 이름")
    private String name;

    @Override
    public CommandResult call() throws Exception {
        DockerImageResponse response = service.push(parent.getPrincipal(), name);
        return CommandResult.builder()
            .console(response.getConsole())
            .status(CommandResultStatus.CREATE)
            .changedImage(response)
            .build();
    }
}
