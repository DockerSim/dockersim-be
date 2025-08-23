package com.dockersim.command.subcommand.image;

import com.dockersim.dto.response.ImageRemoveResponse;
import com.dockersim.service.image.DockerImageService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "rmi", description = "Remove one or more images")
public class ImageRemoveCommand implements Callable<List<ImageRemoveResponse>> {

    private final DockerImageService dockerImageService;

    @Parameters(index = "0..*", description = "The name or ID of the image to remove")
    private List<String> imageNamesOrIds;

    public ImageRemoveCommand(DockerImageService dockerImageService) {
        this.dockerImageService = dockerImageService;
    }

    @Override
    public List<ImageRemoveResponse> call() throws Exception {
        List<ImageRemoveResponse> responses = new ArrayList<>();
        for (String imageNameOrId : imageNamesOrIds) {
            responses.add(dockerImageService.removeImage(imageNameOrId));
        }
        return responses;
    }
}
