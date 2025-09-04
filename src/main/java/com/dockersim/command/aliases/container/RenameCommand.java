package com.dockersim.command.aliases.container;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "rename", description = "컨테이너의 이름을 변경합니다.")
public class RenameCommand {
    // TODO: Implement command logic
}
