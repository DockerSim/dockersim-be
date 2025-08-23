package com.dockersim.command.subcommand.image;

import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.service.image.DockerImageService;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "pull",
    description = "Download an image from a registry",
    mixinStandardHelpOptions = true
)
@RequiredArgsConstructor
public class ImagePullCommand implements Callable<CommandResult> {

    private final DockerImageService imageService;

    @CommandLine.Parameters(index = "0", description = "Name and optionally a tag in the 'name:tag' format")
    private String imageName;

    @Override
    public CommandResult call() {
        DockerImageResponse response = imageService.pullImage(imageName);

        return CommandResult.builder()
            .changedImage(response)
            .build();
    }
}
