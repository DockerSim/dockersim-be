package com.dockersim.command.aliases.container;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "commit", description = "실행 중인 컨테이너로부터 현재 상태를 기반으로 새로운 이미지를 생성합니다.")
public class CommitCommand {
    // TODO: Implement command logic
}