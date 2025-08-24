package com.dockersim.command.aliases.image;

import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.ImageListResponse;
import com.dockersim.service.image.DockerImageService;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(name = "images",
    description = "List images"
)
@RequiredArgsConstructor
public class ImagesCommand implements Callable<CommandResult> {

    private final DockerImageService dockerImageService;

    @Option(names = {"-a", "--all"}, description = "댕글링 이미지를 포함합니다.")
    private boolean all;

    @Option(names = {"-q", "--quiet"}, description = "이미지 ID만 출력합니다.")
    private boolean quiet;

    @Override
    public CommandResult call() {
        ImageListResponse response = dockerImageService.listImages(all, quiet);
        
        return CommandResult.builder()
            .console(response.getConsole())
            .build();
    }
}
