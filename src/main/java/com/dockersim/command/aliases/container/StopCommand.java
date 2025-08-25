package com.dockersim.command.aliases.container;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "stop", description = "실행 중인 컨테이너를 중지합니다.")
public class StopCommand {
    // TODO: Implement command logic
}