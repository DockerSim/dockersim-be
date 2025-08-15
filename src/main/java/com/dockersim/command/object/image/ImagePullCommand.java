package com.dockersim.command.object.image;

import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.executor.CommandResult;
import com.dockersim.service.image.DockerImageServiceImpl;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "pull",
    description = "Download an image from a registry"
)
@RequiredArgsConstructor
public class ImagePullCommand implements Callable<CommandResult> {

    private final DockerImageServiceImpl imageService;

    @CommandLine.Parameters(index = "0", description = "Name and optionally a tag in the 'name:tag' format")
    private String imageName;

    @Override
    public CommandResult call() {
        System.out.println(imageName);
        DockerImageResponse response = imageService.pullImage(imageName);
        System.out.println(response.getConsole());

        return CommandResult.builder()
            .console(response.getConsole())
            .changedImage(response)
            .build();
    }
}
