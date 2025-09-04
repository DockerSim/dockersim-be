package com.dockersim.command.aliases.container;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "export", description = "컨테이너의 파일 시스템을 tar 아카이브로 생성합니다.")
public class ExportCommand {
    // TODO: Implement command logic
}