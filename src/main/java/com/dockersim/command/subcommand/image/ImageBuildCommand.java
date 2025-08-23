package com.dockersim.command.subcommand.image;

import com.dockersim.service.image.DockerImageService;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "build", description = "Build an image from a Dockerfile")
public class ImageBuildCommand implements Callable<String> {

    private final DockerImageService dockerImageService;

    @Option(names = {"-t",
        "--tag"}, description = "Name and optionally a tag in the 'name:tag' format")
    private String name;

    public ImageBuildCommand(DockerImageService dockerImageService) {
        this.dockerImageService = dockerImageService;
    }

    @Override
    public String call() {
        return dockerImageService.buildImage(name);
    }
}
