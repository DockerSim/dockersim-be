package com.dockersim.parser;

/**
 * Docker 명령어 파서 인터페이스
 */
public interface DockerCommandParser {

    /**
     * Docker 명령어 문자열을 파싱하여 ParsedDockerCommand 객체로 변환
     * 
     * @param commandLine Docker 명령어 문자열 (예: "docker run -d nginx")
     * @return 파싱된 명령어 객체
     */
    ParsedDockerCommand parse(String commandLine);

    /**
     * 명령어가 지원되는지 확인
     * 
     * @param command 확인할 명령어 (예: "run", "ps")
     * @return 지원 여부
     */
    boolean isSupported(String command);
}