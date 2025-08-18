package com.dockersim.command.object.image;


import static com.dockersim.command.option.DockerOption.Constants.ALL_TAGS_DESC;
import static com.dockersim.command.option.DockerOption.Constants.ALL_TAGS_LONG;
import static com.dockersim.command.option.DockerOption.Constants.ALL_TAGS_SHORT;
import static com.dockersim.command.option.DockerOption.Constants.DISABLE_CONTENT_TRUST_DESC;
import static com.dockersim.command.option.DockerOption.Constants.DISABLE_CONTENT_TRUST_LONG;
import static com.dockersim.command.option.DockerOption.Constants.PLATFORM;
import static com.dockersim.command.option.DockerOption.Constants.PLATFORM_DESC;

import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.service.image.DockerImageServiceImpl;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Option;


@Component
@CommandLine.Command(name = "pull",
    description = "Download an image from a registry",
    mixinStandardHelpOptions = true
)
@RequiredArgsConstructor
public class ImagePullCommand implements Callable<CommandResult> {

    private final DockerImageServiceImpl imageService;

    @Option(names = {ALL_TAGS_SHORT, ALL_TAGS_LONG}, description = ALL_TAGS_DESC)
    private boolean allTags;

    @Option(names = DISABLE_CONTENT_TRUST_LONG, description = DISABLE_CONTENT_TRUST_DESC)
    private boolean disableContentTrust;

    @Option(names = PLATFORM, description = PLATFORM_DESC)
    private String platform;

    @CommandLine.Parameters(index = "0", description = "Name and optionally a tag in the 'name:tag' format")
    private String imageName;

    @Override
    public CommandResult call() {
        DockerImageResponse response = null;
        if (allTags) {
//            response = imageService.pullAllImage(imageName);
        } else {
            response = imageService.pullImage(imageName);
        }

        return CommandResult.builder()
            .console(response.getConsole())
            .changedImage(response)
            .build();
    }
}
