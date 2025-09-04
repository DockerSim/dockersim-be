package com.dockersim.command.aliases.container;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "cp", description = "호스트와 컨테이너 간 파일을 복사합니다.")
public class CpCommand {
    // TODO: Implement command logic
}
