package com.dockersim.command.aliases.container;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "restart", description = "컨테이너를 재시작합니다.")
public class RestartCommand {
    // TODO: Implement command logic
}
