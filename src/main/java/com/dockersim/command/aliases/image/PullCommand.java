package com.dockersim.command.aliases.image;


import com.dockersim.command.DockerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.service.image.DockerImageService;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Command(name = "pull")
@Component
@RequiredArgsConstructor
public class PullCommand implements Callable<CommandResult> {

    private final DockerImageService service;

    @ParentCommand
    private DockerCommand parent;

    @Option(names = {"-a", "--all"}, description = "동일한 이름을 가진 모든 이미지를 반환")
    private boolean all;

    @CommandLine.Parameters(index = "0", description = "Docker Image 이름")
    private String name;

    @Override
    public CommandResult call() throws Exception {
        List<DockerImageResponse> pull = service.pull(parent.getPrincipal(), name, all);
        return CommandResult.builder()
            .console(pull.stream()
                .flatMap(response -> response.getConsole().stream()).toList())
            .status(CommandResultStatus.CREATE)
            .changedImages(pull)
            .build();
    }
}
