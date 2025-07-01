// 이 클래스는 ContainerService 인터페이스의 구현체입니다.
// 주요 메서드:
// - executeRunCommand : docker run 명령어 실행 - 컨테이너 생성 및 실행
// - executeStopCommand : docker stop 명령어 실행 - 컨테이너 중지
// - executeStartCommand : docker start 명령어 실행 - 컨테이너 시작
// - executeRemoveCommand : docker rm 명령어 실행 - 컨테이너 삭제

package com.dockersim.service.impl;

import com.dockersim.domain.Container;
import com.dockersim.domain.ContainerStatus;
import com.dockersim.domain.Image;
import com.dockersim.dto.CommandExecuteResult;
import com.dockersim.parser.ParsedDockerCommand;
import com.dockersim.repository.ContainerRepository;
import com.dockersim.repository.ImageRepository;
import com.dockersim.service.ContainerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ContainerServiceImpl implements ContainerService {

    private final ContainerRepository containerRepository;
    private final ImageRepository imageRepository;

    public ContainerServiceImpl(ContainerRepository containerRepository, ImageRepository imageRepository) {
        this.containerRepository = containerRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    public CommandExecuteResult executeRunCommand(ParsedDockerCommand command) {
        try {
            // 이미지 이름 추출
            List<String> args = command.getPositionalArgs();
            if (args.isEmpty()) {
                return new CommandExecuteResult(false, "실행할 이미지 이름을 지정해주세요.");
            }

            String imageName = args.get(0);
            String[] parts = imageName.split(":");
            String name = parts[0];
            String tag = parts.length > 1 ? parts[1] : "latest";

            // 이미지 존재 확인
            Optional<Image> imageOpt = imageRepository.findByNameAndTag(name, tag);
            if (imageOpt.isEmpty()) {
                return new CommandExecuteResult(false, "이미지를 찾을 수 없습니다: " + imageName);
            }

            // 컨테이너 생성
            String containerId = generateContainerId();
            String containerName = extractContainerName(command);

            Container container = new Container(containerId, containerName, imageName, ContainerStatus.RUNNING);

            // 옵션 처리
            processRunOptions(command, container);

            // 컨테이너 저장
            containerRepository.save(container);

            return new CommandExecuteResult(
                    true,
                    "컨테이너가 성공적으로 실행되었습니다: " + containerId,
                    containerId);

        } catch (Exception e) {
            return new CommandExecuteResult(false, "컨테이너 실행 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public CommandExecuteResult executeStopCommand(ParsedDockerCommand command) {
        try {
            List<String> args = command.getPositionalArgs();
            if (args.isEmpty()) {
                return new CommandExecuteResult(false, "중지할 컨테이너 ID를 지정해주세요.");
            }

            String containerId = args.get(0);
            Optional<Container> containerOpt = containerRepository.findByContainerId(containerId);

            if (containerOpt.isEmpty()) {
                return new CommandExecuteResult(false, "컨테이너를 찾을 수 없습니다: " + containerId);
            }

            Container container = containerOpt.get();
            if (container.getStatus() != ContainerStatus.RUNNING) {
                return new CommandExecuteResult(false, "컨테이너가 실행 중이 아닙니다: " + containerId);
            }

            container.stop();
            containerRepository.save(container);

            return new CommandExecuteResult(
                    true,
                    "컨테이너가 성공적으로 중지되었습니다: " + containerId,
                    containerId);

        } catch (Exception e) {
            return new CommandExecuteResult(false, "컨테이너 중지 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public CommandExecuteResult executeStartCommand(ParsedDockerCommand command) {
        try {
            List<String> args = command.getPositionalArgs();
            if (args.isEmpty()) {
                return new CommandExecuteResult(false, "시작할 컨테이너 ID를 지정해주세요.");
            }

            String containerId = args.get(0);
            Optional<Container> containerOpt = containerRepository.findByContainerId(containerId);

            if (containerOpt.isEmpty()) {
                return new CommandExecuteResult(false, "컨테이너를 찾을 수 없습니다: " + containerId);
            }

            Container container = containerOpt.get();
            if (container.getStatus() == ContainerStatus.RUNNING) {
                return new CommandExecuteResult(false, "컨테이너가 이미 실행 중입니다: " + containerId);
            }

            container.start();
            containerRepository.save(container);

            return new CommandExecuteResult(
                    true,
                    "컨테이너가 성공적으로 시작되었습니다: " + containerId,
                    containerId);

        } catch (Exception e) {
            return new CommandExecuteResult(false, "컨테이너 시작 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public CommandExecuteResult executeRemoveCommand(ParsedDockerCommand command) {
        try {
            List<String> args = command.getPositionalArgs();
            if (args.isEmpty()) {
                return new CommandExecuteResult(false, "삭제할 컨테이너 ID를 지정해주세요.");
            }

            String containerId = args.get(0);
            Optional<Container> containerOpt = containerRepository.findByContainerId(containerId);

            if (containerOpt.isEmpty()) {
                return new CommandExecuteResult(false, "컨테이너를 찾을 수 없습니다: " + containerId);
            }

            Container container = containerOpt.get();
            if (container.getStatus() == ContainerStatus.RUNNING) {
                // --force 옵션 확인
                boolean force = command.getFlags().contains("-f") || command.getFlags().contains("--force");
                if (!force) {
                    return new CommandExecuteResult(false, "실행 중인 컨테이너는 삭제할 수 없습니다. --force 옵션을 사용하세요.");
                }
            }

            containerRepository.delete(container);

            return new CommandExecuteResult(
                    true,
                    "컨테이너가 성공적으로 삭제되었습니다: " + containerId,
                    containerId);

        } catch (Exception e) {
            return new CommandExecuteResult(false, "컨테이너 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private String generateContainerId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    private String extractContainerName(ParsedDockerCommand command) {
        Map<String, List<String>> options = command.getOptions();
        if (options.containsKey("--name")) {
            return options.get("--name").get(0);
        }
        return null;
    }

    private void processRunOptions(ParsedDockerCommand command, Container container) {
        Map<String, List<String>> options = command.getOptions();
        List<String> flags = command.getFlags();

        // 포트 매핑 처리
        if (options.containsKey("-p")) {
            container.getPorts().addAll(options.get("-p"));
        }

        // 환경 변수 처리
        if (options.containsKey("-e")) {
            container.getEnvironment().addAll(options.get("-e"));
        }

        // 플래그 처리
        if (flags.contains("-d") || flags.contains("--detach")) {
            container.setDetached(true);
        }

        if (flags.contains("--rm")) {
            container.setAutoRemove(true);
        }
    }
}