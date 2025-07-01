// 이 인터페이스는 컨테이너 관련 명령어 처리를 담당합니다.
// 주요 메서드:
// - executeRunCommand : docker run 명령어 실행
// - executeStopCommand : docker stop 명령어 실행
// - executeStartCommand : docker start 명령어 실행
// - executeRemoveCommand : docker rm 명령어 실행

package com.dockersim.service;

import com.dockersim.parser.ParsedDockerCommand;
import com.dockersim.dto.CommandExecuteResult;

public interface ContainerService {

    CommandExecuteResult executeRunCommand(ParsedDockerCommand command);

    CommandExecuteResult executeStopCommand(ParsedDockerCommand command);

    CommandExecuteResult executeStartCommand(ParsedDockerCommand command);

    CommandExecuteResult executeRemoveCommand(ParsedDockerCommand command);
}