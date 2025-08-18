package com.dockersim.command.legacy;

import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.service.image.DockerImageService;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "images", description = "List images",
    mixinStandardHelpOptions = true)
@RequiredArgsConstructor
public class ImagesCommand implements Callable<CommandResult> {

    private final DockerImageService service;

    @Option(names = {"-a",
        "--all"}, description = "Show all images (default hides intermediate images)")
    private boolean all;

    @Option(names = "--digests", description = "unsupported")
    private boolean digests;

    @Option(names = {"-f", "--filter"}, description = "unsupported")
    private String filter;

    @Option(names = "--format", description = "unsupported")
    private String format;

    @Option(names = "--no-trunc", description = "Don't truncate output")
    private boolean noTrunc;

    @Option(names = {"-q", "--quiet"}, description = "Only show image IDs")
    private boolean quiet;

    @Option(names = "--tree", description = "unsupported")
    private boolean tree;

    @Parameters(index = "0", arity = "0..1", description = "Filter images by repository")
    private String name;

    @Override
    public CommandResult call() {
        List<DockerImageResponse> images = service.listImages(name, all, quiet);

        return CommandResult.builder()
            .changedImages(images)
            .build();
    }
}
