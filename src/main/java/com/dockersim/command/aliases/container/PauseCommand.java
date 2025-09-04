package com.dockersim.command.aliases.container;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "pause", description = "컨테이너의 모든 프로세스를 일시 정지합니다.")
public class PauseCommand {
    // TODO: Implement command logic
}