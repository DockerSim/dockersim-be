package com.dockersim.command.toplevel;

import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.ImageListResponse;
import com.dockersim.service.image.DockerImageService;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "images",
    description = "List images (alias for 'docker image ls')"
)
@RequiredArgsConstructor
public class ImagesCommand implements Callable<CommandResult> {

    private final DockerImageService dockerImageService;

    @Override
    public CommandResult call() throws Exception {
        ImageListResponse response = dockerImageService.listImages();
        return CommandResult.builder()
            .console(response.getConsole())
            .build();
    }
}
