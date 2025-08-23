package com.dockersim.command.subcommand.image;

import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.service.image.DockerImageService;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "push",
    description = "Push an image or a repository to a registry")
@RequiredArgsConstructor
public class ImagePushCommand implements Callable<CommandResult> {

    private final DockerImageService dockerImageService;

    @Parameters(index = "0", description = "Name and optionally a tag in the 'name:tag' format")
    private String imageName;

    @Override
    public CommandResult call() {
        DockerImageResponse response = dockerImageService.pushImage(imageName);
        return CommandResult.builder()
            .changedImage(response)
            .build();
    }
}
