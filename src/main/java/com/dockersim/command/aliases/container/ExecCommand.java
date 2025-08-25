package com.dockersim.command.aliases.container;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "exec", description = "실행 중인 컨테이너 내부에서 추가 명령을 실행합니다.")
public class ExecCommand {
    // TODO: Implement command logic
}
