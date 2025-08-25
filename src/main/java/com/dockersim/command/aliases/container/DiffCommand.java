package com.dockersim.command.aliases.container;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "diff", description = "컨테이너 파일 시스템의 변경점을 출력합니다.")
public class DiffCommand {
    // TODO: Implement command logic
}
