package com.dockersim.parser;

import java.util.List;

public interface DockerCommandParser {

    /**
     * 도커 명령어 문자열을 토큰화하여 리스트로 반환
     *
     * @param commandLine 파싱할 도커 명령어 문자열
     * @return 파싱된 토큰들의 리스트
     */
    List<String> tokenize(String commandLine);
}
