package com.dockersim.command.aliases.container;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "rm", aliases = {"remove"}, description = "하나 이상의 컨테이너를 삭제합니다.")
public class RmCommand {
    // TODO: Implement command logic
}
