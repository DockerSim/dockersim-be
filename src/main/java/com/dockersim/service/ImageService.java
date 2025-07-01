// 이 인터페이스는 이미지 관련 명령어 처리를 담당합니다.
// 주요 메서드:
// - executePullCommand : docker pull 명령어 실행
// - executeRemoveCommand : docker rmi 명령어 실행
// - executeBuildCommand : docker build 명령어 실행
// - executeListCommand : docker images 명령어 실행

package com.dockersim.service;

import com.dockersim.parser.ParsedDockerCommand;
import com.dockersim.dto.CommandExecuteResult;

public interface ImageService {

    CommandExecuteResult executePullCommand(ParsedDockerCommand command);

    CommandExecuteResult executeRemoveCommand(ParsedDockerCommand command);

    CommandExecuteResult executeBuildCommand(ParsedDockerCommand command);

    CommandExecuteResult executeListCommand(ParsedDockerCommand command);
}