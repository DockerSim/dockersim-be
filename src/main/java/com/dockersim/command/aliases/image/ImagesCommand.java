package com.dockersim.command.aliases.image;

import com.dockersim.command.DockerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.service.image.DockerImageService;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Component
@Command(name = "images")
@RequiredArgsConstructor
public class ImagesCommand implements Callable<CommandResult> {

    private final DockerImageService service;

    @ParentCommand
    private DockerCommand parent;

    @Option(names = {"-a", "--all"}, description = "댕글링 이미지를 포함합니다.")
    private boolean all;

    @Option(names = {"-q", "--quiet"}, description = "Hex ID만 출력합니다.")
    private boolean quiet;

    @Override
    public CommandResult call() throws Exception {
        return CommandResult.builder()
            .console(service.ls(parent.getPrincipal(), all, quiet))
            .status(CommandResultStatus.READ)
            .build();
    }
}