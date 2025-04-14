package com.dockersim.service;

import com.dockersim.common.exception.DockerCommandException;
import com.dockersim.common.response.DockerParserCode;
import com.dockersim.dto.ParsedCommand;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DockerCommandParser {
    public static ParsedCommand parseCommand(String rawCommand) {
        if (rawCommand == null || rawCommand.trim().isEmpty()) {
            throw new DockerCommandException(DockerParserCode.EMPTY_COMMAND);
        }

        List<String> tokens = Arrays.stream(rawCommand.trim().split("\\s+")).toList();
        if (!tokens.get(0).startsWith("docker")) {
            throw new DockerCommandException(DockerParserCode.NOT_START_WITH_DOCKER_COMMAND);
        }
        if (tokens.size() < 2 || tokens.get(1).equals("--help")) {
            throw new DockerCommandException(DockerParserCode.MISSING_SUBCOMMAND);
        }
        /*
        유효한 그룹/커멘드 분류 및 확인
        지원 여부 확인
         */
        // 만약 그룹 명령어라면 커맨드 확인

        // 만약 커맨드 명령어라면


        ParsedCommand parsed = new ParsedCommand();

        return parsed;
    }
}
