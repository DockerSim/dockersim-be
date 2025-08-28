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
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Command(name = "build")
@Component
@RequiredArgsConstructor
public class BuildCommand implements Callable<CommandResult> {

    private final DockerImageService service;

    @ParentCommand
    private DockerCommand parent;

    @Option(names = {"-t", "--tag"}, description = "새로 생성하는 Docker Image 이름")
    private String name;

    @CommandLine.Parameters(index = "0", description = "DockerFile 경로")
    private String path;

    @Override
    public CommandResult call() throws Exception {
        DockerImageResponse response = service.build(parent.getPrincipal(), path, name);
        return CommandResult.builder()
            .console(response.getConsole())
            .status(CommandResultStatus.CREATE)
            .changedImage(response)
            .build();
    }
}