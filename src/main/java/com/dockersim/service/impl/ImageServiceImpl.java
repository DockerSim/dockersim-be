// 이 클래스는 ImageService 인터페이스의 구현체입니다.
// 주요 메서드:
// - executePullCommand : docker pull 명령어 실행 - 이미지 다운로드
// - executeRemoveCommand : docker rmi 명령어 실행 - 이미지 삭제
// - executeBuildCommand : docker build 명령어 실행 - 이미지 빌드
// - executeListCommand : docker images 명령어 실행 - 이미지 목록 조회

package com.dockersim.service.impl;

import com.dockersim.domain.Image;
import com.dockersim.dto.CommandExecuteResult;
import com.dockersim.parser.ParsedDockerCommand;
import com.dockersim.repository.ImageRepository;
import com.dockersim.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public CommandExecuteResult executePullCommand(ParsedDockerCommand command) {
        try {
            List<String> args = command.getPositionalArgs();
            if (args.isEmpty()) {
                return new CommandExecuteResult(false, "다운로드할 이미지 이름을 지정해주세요.");
            }

            String imageName = args.get(0);
            String[] parts = imageName.split(":");
            String name = parts[0];
            String tag = parts.length > 1 ? parts[1] : "latest";

            // 이미지가 이미 존재하는지 확인
            if (imageRepository.existsByNameAndTag(name, tag)) {
                return new CommandExecuteResult(
                        true,
                        "이미지가 이미 최신 상태입니다: " + name + ":" + tag,
                        null);
            }

            // 새 이미지 생성
            String imageId = generateImageId();
            Long size = generateRandomSize();

            Image image = new Image(imageId, name, tag, size);
            imageRepository.save(image);

            return new CommandExecuteResult(
                    true,
                    "이미지가 성공적으로 다운로드되었습니다: " + name + ":" + tag,
                    imageId);

        } catch (Exception e) {
            return new CommandExecuteResult(false, "이미지 다운로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public CommandExecuteResult executeRemoveCommand(ParsedDockerCommand command) {
        try {
            List<String> args = command.getPositionalArgs();
            if (args.isEmpty()) {
                return new CommandExecuteResult(false, "삭제할 이미지 이름을 지정해주세요.");
            }

            String imageName = args.get(0);
            String[] parts = imageName.split(":");
            String name = parts[0];
            String tag = parts.length > 1 ? parts[1] : "latest";

            Optional<Image> imageOpt = imageRepository.findByNameAndTag(name, tag);
            if (imageOpt.isEmpty()) {
                return new CommandExecuteResult(false, "이미지를 찾을 수 없습니다: " + imageName);
            }

            Image image = imageOpt.get();
            imageRepository.delete(image);

            return new CommandExecuteResult(
                    true,
                    "이미지가 성공적으로 삭제되었습니다: " + imageName,
                    image.getImageId());

        } catch (Exception e) {
            return new CommandExecuteResult(false, "이미지 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public CommandExecuteResult executeBuildCommand(ParsedDockerCommand command) {
        try {
            // 기본 이미지 정보
            String name = "myapp";
            String tag = "latest";

            // -t 옵션에서 이미지 이름 추출
            if (command.getOptions().containsKey("-t")) {
                String fullName = command.getOptions().get("-t").get(0);
                String[] parts = fullName.split(":");
                name = parts[0];
                tag = parts.length > 1 ? parts[1] : "latest";
            }

            String imageId = generateImageId();
            Long size = generateRandomSize();

            Image image = new Image(imageId, name, tag, size);
            imageRepository.save(image);

            return new CommandExecuteResult(
                    true,
                    "이미지가 성공적으로 빌드되었습니다: " + name + ":" + tag,
                    imageId);

        } catch (Exception e) {
            return new CommandExecuteResult(false, "이미지 빌드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public CommandExecuteResult executeListCommand(ParsedDockerCommand command) {
        try {
            List<Image> images = imageRepository.findAll();

            if (images.isEmpty()) {
                return new CommandExecuteResult(
                        true,
                        "REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE",
                        null);
            }

            StringBuilder result = new StringBuilder();
            result.append("REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE\n");

            for (Image image : images) {
                result.append(String.format("%-19s %-19s %-19s %-19s %s\n",
                        image.getName(),
                        image.getTag(),
                        image.getImageId().substring(7, 19), // sha256: 제거하고 12자리만
                        "2 minutes ago",
                        formatSize(image.getSize())));
            }

            return new CommandExecuteResult(true, result.toString().trim(), null);

        } catch (Exception e) {
            return new CommandExecuteResult(false, "이미지 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private String generateImageId() {
        return "sha256:" + UUID.randomUUID().toString().replace("-", "") +
                UUID.randomUUID().toString().replace("-", "");
    }

    private Long generateRandomSize() {
        return (long) (Math.random() * 500 + 50) * 1024 * 1024; // 50MB ~ 550MB
    }

    private String formatSize(Long sizeInBytes) {
        if (sizeInBytes < 1024 * 1024) {
            return (sizeInBytes / 1024) + "KB";
        } else if (sizeInBytes < 1024 * 1024 * 1024) {
            return (sizeInBytes / (1024 * 1024)) + "MB";
        } else {
            return String.format("%.1fGB", sizeInBytes / (1024.0 * 1024 * 1024));
        }
    }
}