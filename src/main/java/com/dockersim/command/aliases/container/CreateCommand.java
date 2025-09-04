package com.dockersim.command.aliases.container;

import com.dockersim.domain.ContainerStatus;
import com.dockersim.dto.request.CreateContainerRequest;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.DockerContainerResponse;
import com.dockersim.service.container.DockerContainerService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "create", description = "새로운 컨테이너를 생성합니다.")
@RequiredArgsConstructor
public class CreateCommand implements Callable<CommandResult> {

    private final DockerContainerService service;

    @Option(names = "--name", description = "컨테이너에 이름을 할당합니다.")
    private String name;

    @Option(names = {"-p", "--publish"}, description = "컨테이너의 포트를 호스트에 게시합니다. (예: 8080:80)")
    private List<String> ports = new ArrayList<>();

    @Option(names = {"-v",
        "--volume"}, description = "볼륨을 바인드 마운트합니다. (예: /host/path:/container/path)")
    private List<String> volumes = new ArrayList<>();

    @Option(names = {"-e", "--env"}, description = "환경 변수를 설정합니다. (예: KEY=VALUE)")
    private List<String> envs = new ArrayList<>();

    @Parameters(index = "0", description = "사용할 컨테이너 이미지 이름")
    private String baseImageNameOrId;


    @Override
    public CommandResult call() throws Exception {
        DockerContainerResponse response = service.create(
            new CreateContainerRequest(baseImageNameOrId, name, ContainerStatus.CREATED,
                ports, volumes, envs)
        );
        return CommandResult.builder()
            .console(response.getConsole())
            .build();
    }
}
