package com.dockersim.command.aliases.container;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "unpause", description = "일시 정지된 컨테이너의 모든 프로세스를 재개합니다.")
public class UnpauseCommand {
    // TODO: Implement command logic
}