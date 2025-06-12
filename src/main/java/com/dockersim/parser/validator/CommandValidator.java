package com.dockersim.parser.validator;

import com.dockersim.dto.ParsedDockerCommand;

import java.util.Arrays;
import java.util.List;

public class CommandValidator {

    private static final List<String> VALID_CONTAINER_COMMANDS = Arrays.asList(
            "run", "start", "stop", "restart", "rm", "ps", "exec", "logs", "inspect", "create");

    private static final List<String> VALID_IMAGE_COMMANDS = Arrays.asList(
            "pull", "push", "images", "rmi", "build", "tag", "history", "inspect", "prune");

    private static final List<String> VALID_NETWORK_COMMANDS = Arrays.asList(
            "create", "rm", "ls", "inspect", "connect", "disconnect");

    private static final List<String> VALID_VOLUME_COMMANDS = Arrays.asList(
            "create", "rm", "ls", "inspect", "prune");

    public String validate(ParsedDockerCommand command) {
        if (command == null) {
            return "명령어를 파싱할 수 없습니다.";
        }

        if (!command.isValid()) {
            return command.getErrorMessage();
        }

        String group = command.getGroup();
        String subCommand = command.getSubCommand();

        if (subCommand == null || subCommand.trim().isEmpty()) {
            return "실행할 명령어를 입력해주세요.";
        }

        // 그룹별 명령어 유효성 검사
        switch (group) {
            case "container":
                if (!VALID_CONTAINER_COMMANDS.contains(subCommand)) {
                    return "알 수 없는 container 명령어입니다: " + subCommand +
                            ". 사용 가능한 명령어: " + String.join(", ", VALID_CONTAINER_COMMANDS);
                }
                return validateContainerCommand(command);

            case "image":
                if (!VALID_IMAGE_COMMANDS.contains(subCommand)) {
                    return "알 수 없는 image 명령어입니다: " + subCommand +
                            ". 사용 가능한 명령어: " + String.join(", ", VALID_IMAGE_COMMANDS);
                }
                return validateImageCommand(command);

            case "network":
                if (!VALID_NETWORK_COMMANDS.contains(subCommand)) {
                    return "알 수 없는 network 명령어입니다: " + subCommand +
                            ". 사용 가능한 명령어: " + String.join(", ", VALID_NETWORK_COMMANDS);
                }
                return validateNetworkCommand(command);

            case "volume":
                if (!VALID_VOLUME_COMMANDS.contains(subCommand)) {
                    return "알 수 없는 volume 명령어입니다: " + subCommand +
                            ". 사용 가능한 명령어: " + String.join(", ", VALID_VOLUME_COMMANDS);
                }
                return validateVolumeCommand(command);

            case "unknown":
                return "지원하지 않는 명령어입니다: " + subCommand;

            default:
                return null; // 유효함
        }
    }

    private String validateContainerCommand(ParsedDockerCommand command) {
        String subCommand = command.getSubCommand();

        switch (subCommand) {
            case "run":
                if (command.getImageName() == null) {
                    return "docker run 명령어에는 이미지 이름이 필요합니다. 예: docker run nginx";
                }

                // 컨테이너 이름 중복 검사는 서비스 레이어에서 처리
                break;

            case "start":
            case "stop":
            case "restart":
            case "rm":
            case "logs":
            case "inspect":
                if (command.getArguments().isEmpty() && command.getContainerName() == null) {
                    return "docker " + subCommand + " 명령어에는 컨테이너 이름 또는 ID가 필요합니다.";
                }
                break;

            case "exec":
                if (command.getArguments().size() < 2) {
                    return "docker exec 명령어에는 컨테이너 이름과 실행할 명령어가 필요합니다. 예: docker exec mycontainer ls";
                }
                break;
        }

        return null; // 유효함
    }

    private String validateImageCommand(ParsedDockerCommand command) {
        String subCommand = command.getSubCommand();

        switch (subCommand) {
            case "pull":
            case "push":
            case "rmi":
            case "history":
            case "inspect":
                if (command.getArguments().isEmpty()) {
                    return "docker " + subCommand + " 명령어에는 이미지 이름이 필요합니다.";
                }
                break;

            case "tag":
                if (command.getArguments().size() < 2) {
                    return "docker tag 명령어에는 원본 이미지와 새 태그가 필요합니다. 예: docker tag nginx mynginx";
                }
                break;

            case "build":
                // Dockerfile 경로 또는 컨텍스트 필요
                if (command.getArguments().isEmpty()) {
                    return "docker build 명령어에는 빌드 컨텍스트가 필요합니다. 예: docker build .";
                }
                break;
        }

        return null; // 유효함
    }

    private String validateNetworkCommand(ParsedDockerCommand command) {
        String subCommand = command.getSubCommand();

        switch (subCommand) {
            case "create":
            case "rm":
            case "inspect":
                if (command.getArguments().isEmpty()) {
                    return "docker network " + subCommand + " 명령어에는 네트워크 이름이 필요합니다.";
                }
                break;

            case "connect":
            case "disconnect":
                if (command.getArguments().size() < 2) {
                    return "docker network " + subCommand + " 명령어에는 네트워크 이름과 컨테이너 이름이 필요합니다.";
                }
                break;
        }

        return null; // 유효함
    }

    private String validateVolumeCommand(ParsedDockerCommand command) {
        String subCommand = command.getSubCommand();

        switch (subCommand) {
            case "create":
            case "rm":
            case "inspect":
                if (command.getArguments().isEmpty()) {
                    return "docker volume " + subCommand + " 명령어에는 볼륨 이름이 필요합니다.";
                }
                break;
        }

        return null; // 유효함
    }
}