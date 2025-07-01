// 이 클래스는 파싱된 Docker 명령어를 적절한 서비스로 라우팅하는 실행기입니다.
// 주요 메서드:
// - execute : 파싱된 명령어를 도메인별로 분류하여 해당 서비스에 전달
// - routeContainerCommand : 컨테이너 관련 명령어를 ContainerService로 라우팅
// - routeImageCommand : 이미지 관련 명령어를 ImageService로 라우팅

package com.dockersim.executor;

import com.dockersim.dto.CommandExecuteResult;
import com.dockersim.parser.ParsedDockerCommand;
import com.dockersim.service.ContainerService;
import com.dockersim.service.ImageService;
import org.springframework.stereotype.Component;

@Component
public class CommandExecutor {

    private final ContainerService containerService;
    private final ImageService imageService;

    public CommandExecutor(ContainerService containerService, ImageService imageService) {
        this.containerService = containerService;
        this.imageService = imageService;
    }

    public CommandExecuteResult execute(ParsedDockerCommand command) {
        String domain = command.getDomain();

        return switch (domain) {
            case "CONTAINER" -> routeContainerCommand(command);
            case "IMAGE" -> routeImageCommand(command);
            case "NETWORK" -> throw new IllegalArgumentException("NETWORK 도메인은 아직 지원되지 않습니다");
            case "VOLUME" -> throw new IllegalArgumentException("VOLUME 도메인은 아직 지원되지 않습니다");
            default -> throw new IllegalArgumentException("지원되지 않는 도메인입니다: " + domain);
        };
    }

    private CommandExecuteResult routeContainerCommand(ParsedDockerCommand command) {
        String subCommand = command.getSubCommand();

        return switch (subCommand) {
            case "run" -> containerService.executeRunCommand(command);
            case "stop" -> containerService.executeStopCommand(command);
            case "start" -> containerService.executeStartCommand(command);
            case "rm" -> containerService.executeRemoveCommand(command);
            default -> throw new IllegalArgumentException("지원되지 않는 CONTAINER 명령어입니다: " + subCommand);
        };
    }

    private CommandExecuteResult routeImageCommand(ParsedDockerCommand command) {
        String subCommand = command.getSubCommand();

        return switch (subCommand) {
            case "pull" -> imageService.executePullCommand(command);
            case "rmi" -> imageService.executeRemoveCommand(command);
            case "build" -> imageService.executeBuildCommand(command);
            case "images" -> imageService.executeListCommand(command);
            default -> throw new IllegalArgumentException("지원되지 않는 IMAGE 명령어입니다: " + subCommand);
        };
    }
}