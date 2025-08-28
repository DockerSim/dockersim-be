package com.dockersim.command.subcommand.image;

import com.dockersim.command.subcommand.ImageCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.service.image.DockerImageService;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Command(name = "prune")
@Component
@RequiredArgsConstructor
public class ImagePruneCommand implements Callable<CommandResult> {

    private final DockerImageService service;

    @ParentCommand
    private ImageCommand parent;

    @Option(names = {"-a", "--all"}, description = "기본 동작 변경: 컨테이너에 연결되지 않는 모든 이미지 삭제")
    private boolean all;

    @Override
    public CommandResult call() throws Exception {
        List<DockerImageResponse> prune = service.prune(parent.getPrincipal(), all);
        return CommandResult.builder()
            .console(prune.stream()
                .flatMap(response -> response.getConsole().stream()).toList())
            .status(CommandResultStatus.DELETE)
            .changedImages(prune)
            .build();
    }
}