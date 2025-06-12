package com.dockersim.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DockerParserCode implements ApiResponseCode {
    EMPTY_COMMAND("P001", "명령어가 입력되지 않았습니다."),
    NOT_START_WITH_DOCKER_COMMAND("P002", "명령어는 'docker'로 시작해야 합니다."),
    MISSING_SUBCOMMAND("P003", "docker 명령어 뒤에 실행할 명령이 없습니다."),
    UNSUPPORTED_COMMAND("P004", "지원하지 않는 명령어입니다."),
    INCOMPLETE_GROUP_COMMAND("P005", "도커 그룹 명령 뒤에 명령어가 누락되었습니다.")
    ;

    private final String code;
    private final String message;
}
