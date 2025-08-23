package com.dockersim.command.subcommand.image;

import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.ImageListResponse;
import com.dockersim.service.image.DockerImageService;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine.Command;

@Command(name = "ls",
    description = "List images"
)
@RequiredArgsConstructor
public class ImageLsCommand implements Callable<CommandResult> {

    private final DockerImageService dockerImageService;

    @Override
    public CommandResult call() throws Exception {
        ImageListResponse response = dockerImageService.listImages();
        return CommandResult.builder()
            .console(response.getConsole())
            .build();
    }
}
