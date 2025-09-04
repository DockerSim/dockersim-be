package com.dockersim.command.aliases.container;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "port", description = "컨테이너의 포트 매핑을 확인합니다.")
public class PortCommand {
    // TODO: Implement command logic
}