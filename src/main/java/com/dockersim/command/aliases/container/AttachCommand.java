package com.dockersim.command.aliases.container;

import com.dockersim.dto.response.CommandResult;
import com.dockersim.service.container.DockerContainerService;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Component
@Command(name = "attach", description = "실행 중인 컨테이너의 표준 터미널에 접속합니다.")
@RequiredArgsConstructor
public class AttachCommand implements Callable<CommandResult> {

    private final DockerContainerService service;

    @CommandLine.Parameters(index = "0", description = "컨테이너의 이름 또는 축약형 ID")
    private String nameOrId;

    @Override
    public CommandResult call() {
        return CommandResult.builder()
            .console(service.attach(nameOrId))
            .build();
    }
}