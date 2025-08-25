package com.dockersim.command.subcommand.container;

import com.dockersim.dto.response.CommandResult;
import com.dockersim.service.container.DockerContainerService;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(name = "ls", aliases = {"list", "ps"}, description = "컨테이너 목록을 조회합니다.")
@RequiredArgsConstructor
public class ContainerLsCommand implements Callable<CommandResult> {

    private final DockerContainerService dockerContainerService;

    @Option(names = {"-a", "--all"}, description = "정지된 컨테이너도 모두 출력합니다.")
    private boolean all = false;

    @Option(names = {"-q", "--quiet"}, description = "컨테이너 ID만 출력합니다.")
    private boolean quiet = false;

    @Override
    public CommandResult call() {
        List<String> result = dockerContainerService.listContainers(all, quiet);
        return CommandResult.builder()
            .console(result)
            .build();
    }
}