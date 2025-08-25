package com.dockersim.command.aliases.container;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "kill", description = "실행 중인 컨테이너를 강제로 중지합니다.")
public class KillCommand {
    // TODO: Implement command logic
}