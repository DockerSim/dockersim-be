package com.dockersim.parser;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * Docker 명령어 파싱 결과를 담는 클래스
 */
@Getter
@Builder
@ToString
public class ParsedDockerCommand {

    /**
     * 메인 명령어 (run, ps, start, stop, etc.)
     */
    private final String command;

    /**
     * 서브 명령어 (network create, volume ls 등에서 create, ls 부분)
     */
    private final String subCommand;

    /**
     * 대상 리소스 (컨테이너명, 이미지명 등)
     */
    private final String target;

    /**
     * 명령어 옵션들 (-d, --name, -p 등)
     */
    private final Map<String, String> options;

    /**
     * 명령어 인자들 (위치 기반)
     */
    private final List<String> arguments;

    /**
     * 원본 명령어 문자열
     */
    private final String originalCommand;

    /**
     * 파싱 성공 여부
     */
    private final boolean valid;

    /**
     * 파싱 오류 메시지 (valid가 false일 때)
     */
    private final String errorMessage;

    // === 편의 메서드들 ===

    /**
     * 특정 옵션의 존재 여부 확인
     */
    public boolean hasOption(String option) {
        return options.containsKey(option);
    }

    /**
     * 특정 옵션의 값 가져오기
     */
    public String getOptionValue(String option) {
        return options.get(option);
    }

    /**
     * 불리언 옵션 확인 (-d, -a 등)
     */
    public boolean isFlagSet(String flag) {
        return options.containsKey(flag) && "true".equals(options.get(flag));
    }

    /**
     * run 명령어인지 확인
     */
    public boolean isRunCommand() {
        return "run".equals(command);
    }

    /**
     * ps 명령어인지 확인
     */
    public boolean isPsCommand() {
        return "ps".equals(command);
    }

    /**
     * 이미지 관련 명령어인지 확인
     */
    public boolean isImageCommand() {
        return "images".equals(command) || "pull".equals(command) || "rmi".equals(command);
    }

    /**
     * 컨테이너 관련 명령어인지 확인
     */
    public boolean isContainerCommand() {
        return "start".equals(command) || "stop".equals(command) ||
                "restart".equals(command) || "rm".equals(command) ||
                "logs".equals(command) || "inspect".equals(command);
    }

    /**
     * 볼륨 관련 명령어인지 확인
     */
    public boolean isVolumeCommand() {
        return "volume".equals(command);
    }

    /**
     * 네트워크 관련 명령어인지 확인
     */
    public boolean isNetworkCommand() {
        return "network".equals(command);
    }
}