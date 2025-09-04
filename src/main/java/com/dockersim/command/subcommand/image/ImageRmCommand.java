package com.dockersim.command.subcommand.image;

import com.dockersim.command.subcommand.ImageCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.service.image.DockerImageService;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

@Command(name = "rm", aliases = "remove")
@Component
@RequiredArgsConstructor
public class ImageRmCommand implements Callable<CommandResult> {

    private final DockerImageService service;

    @ParentCommand
    private ImageCommand parent;

    @Option(names = {"-f", "--force"},
        description = "기본 동작 변경: 삭제하려는 Docker Image 를 기반으로 생성된 Docker Container 가 있어도 Docker Image 삭제")
    private boolean all;

    @Parameters(index = "0", description = "도커 이미지 이름 또는 Hex ID")
    private String nameOrHexId;

    @Override
    public CommandResult call() throws Exception {
        DockerImageResponse response = service.rm(parent.getPrincipal(), nameOrHexId, all);
        return CommandResult.builder()
            .console(response.getConsole())
            .status(CommandResultStatus.DELETE)
            .changedImage(response)
            .build();
    }
}
