// 이 클래스는 파싱된 Docker 명령어 정보를 담는 데이터 객체입니다.
// 주요 필드:
// - subCommand : docker의 하위 명령어 (run, pull, stop 등)
// - flags : 단일 플래그 리스트 (-d, --rm 등)
// - options : 키-값 옵션 맵 (-p 8080:80, --name web 등)
// - positionalArgs : 위치 인자 리스트 (이미지명, 컨테이너명 등)
// - domain : 명령어가 속하는 도메인 (CONTAINER, IMAGE, NETWORK, VOLUME)

package com.dockersim.parser;

import java.util.List;
import java.util.Map;

public class ParsedDockerCommand {

    private final String subCommand;
    private final List<String> flags;
    private final Map<String, List<String>> options;
    private final List<String> positionalArgs;
    private final String domain;

    public ParsedDockerCommand(String subCommand,
            List<String> flags,
            Map<String, List<String>> options,
            List<String> positionalArgs,
            String domain) {
        this.subCommand = subCommand;
        this.flags = flags;
        this.options = options;
        this.positionalArgs = positionalArgs;
        this.domain = domain;
    }

    public String getSubCommand() {
        return subCommand;
    }

    public List<String> getFlags() {
        return flags;
    }

    public Map<String, List<String>> getOptions() {
        return options;
    }

    public List<String> getPositionalArgs() {
        return positionalArgs;
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public String toString() {
        return "ParsedDockerCommand{" +
                "subCommand='" + subCommand + '\'' +
                ", flags=" + flags +
                ", options=" + options +
                ", positionalArgs=" + positionalArgs +
                ", domain='" + domain + '\'' +
                '}';
    }
}