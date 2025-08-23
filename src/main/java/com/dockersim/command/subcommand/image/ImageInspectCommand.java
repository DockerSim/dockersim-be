package com.dockersim.command.subcommand.image;

import com.dockersim.dto.response.CommandResult;
import com.dockersim.service.image.DockerImageService;
import java.util.Collections;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "inspect", description = "Display detailed information on one or more images")
public class ImageInspectCommand implements Callable<CommandResult> {

    private final DockerImageService dockerImageService;

    @Parameters(index = "0", description = "Name or ID of the image to inspect")
    private String imageNameOrId;

    public ImageInspectCommand(DockerImageService dockerImageService) {
        this.dockerImageService = dockerImageService;
    }

    @Override
    public CommandResult call() throws Exception {
        String inspectionResult = dockerImageService.inspectImage(imageNameOrId);
        return CommandResult.builder()
            .console(Collections.singletonList(inspectionResult))
            .build();
    }
}
