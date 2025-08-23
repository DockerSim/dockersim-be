package com.dockersim.command.subcommand.image;

import com.dockersim.dto.response.CommandResult;
import com.dockersim.service.image.DockerImageService;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import picocli.CommandLine.Command;

@Command(name = "prune", description = "Remove unused images")
public class ImagePruneCommand implements Callable<CommandResult> {

    private final DockerImageService dockerImageService;

    public ImagePruneCommand(DockerImageService dockerImageService) {
        this.dockerImageService = dockerImageService;
    }

    @Override
    public CommandResult call() throws Exception {
        List<String> prunedImageIds = dockerImageService.pruneImages();

        List<String> consoleOutput = prunedImageIds.stream()
            .map(id -> "Deleted: " + id)
            .collect(Collectors.toList());

        if (!consoleOutput.isEmpty()) {
            consoleOutput.add(0, "The following images were pruned:");
            // In a real scenario, we would calculate the actual space reclaimed.
            consoleOutput.add("Total reclaimed space: 0B");
        } else {
            consoleOutput.add("Total reclaimed space: 0B");
        }

        return CommandResult.builder()
            .console(consoleOutput)
            .build();
    }
}
