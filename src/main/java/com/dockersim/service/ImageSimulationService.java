package com.dockersim.service;

import com.dockersim.dto.ParsedDockerCommand;
import com.dockersim.dto.response.CommandExecuteResponse;
import com.dockersim.dto.response.ImageSimulationDto;
import com.dockersim.entity.ImageSimulation;
import com.dockersim.entity.enums.ImageSource;
import com.dockersim.repository.ImageSimulationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Docker 이미지 관련 명령어 시뮬레이션 서비스
 * 
 * 이 클래스는 Docker 이미지 관리 명령어들을 교육 목적으로 시뮬레이션합니다.
 * 실제 Docker 엔진과 동일한 동작을 모방하여 학습자가 Docker 이미지 개념을 이해할 수 있도록 돕습니다.
 * 
 * 지원하는 명령어 및 기능:
 * 
 * 1. docker pull [OPTIONS] IMAGE[:TAG]
 * - 공식/비공식 이미지 다운로드 시뮬레이션
 * - 지원 옵션: 없음 (기본 pull 동작만)
 * - 처리 내용: DockerHub 공식 이미지 검증, 네임스페이스 분석, 이미지 생성
 * - 예시: docker pull nginx, docker pull library/ubuntu:20.04
 * 
 * 2. docker build [OPTIONS] PATH
 * - Dockerfile을 이용한 이미지 빌드 시뮬레이션
 * - 지원 옵션:
 * * -t, --tag: 이미지명과 태그 지정 (예: myapp:v1.0, registry/myapp:latest)
 * * --build-arg: 빌드 인자 전달 (출력에만 반영, 실제 처리 없음)
 * * --no-cache: 캐시 없이 빌드 (출력 메시지만 변경)
 * * --pull: 최신 베이스 이미지 사용 (출력 메시지만 변경)
 * * --platform: 플랫폼 지정 (출력 메시지만 변경)
 * - 처리 내용: 8단계 빌드 과정 시뮬레이션, 동일 태그 덮어쓰기, 익명 이미지 생성
 * - 예시: docker build ., docker build -t myapp:v1.0 ., docker build --no-cache
 * -t myapp .
 * 
 * 3. docker tag SOURCE_IMAGE[:TAG] TARGET_IMAGE[:TAG]
 * - 기존 이미지에 새로운 태그 생성
 * - 지원 옵션: 없음 (기본 tag 동작만)
 * - 처리 내용: 소스 이미지 존재 확인, 새 태그로 이미지 복사, 네임스페이스 처리
 * - 예시: docker tag myapp:v1.0 myapp:latest, docker tag nginx:1.20
 * myregistry/nginx:stable
 * 
 * 4. docker push [OPTIONS] NAME[:TAG]
 * - 네임스페이스가 있는 이미지를 레지스트리에 업로드 시뮬레이션
 * - 지원 옵션: 없음 (기본 push 동작만)
 * - 처리 내용: 네임스페이스 필수 검증, 이미지 존재 확인, push 과정 시뮬레이션
 * - 제한사항: 네임스페이스 없는 이미지는 push 불가 (에러 메시지 제공)
 * - 예시: docker push myregistry/myapp:v1.0 (성공), docker push myapp:v1.0 (실패)
 * 
 * 5. docker inspect [OPTIONS] NAME[:TAG]
 * - 이미지 메타데이터 조회 (JSON 형태)
 * - 지원 옵션: 없음 (기본 inspect 동작만)
 * - 처리 내용: 이미지 존재 확인, 상세 JSON 메타데이터 생성 (Id, RepoTags, Config 등)
 * - 출력 형태: 실제 Docker와 유사한 JSON 구조
 * - 예시: docker inspect nginx:latest, docker inspect myapp:v1.0
 * 
 * 6. docker image prune [OPTIONS]
 * - 불필요한 이미지 정리 시뮬레이션
 * - 지원 옵션:
 * * -f, --force: 확인 없이 강제 삭제
 * * -a, --all: 모든 미사용 이미지 삭제 (댕글링 이미지만이 아닌)
 * - 처리 내용: 댕글링 이미지 찾기, 조건에 따른 이미지 삭제, 용량 계산
 * - 예시: docker image prune -f, docker image prune -a -f
 * 
 * 7. docker images [OPTIONS] [REPOSITORY[:TAG]]
 * - 로컬 이미지 목록 조회
 * - 지원 옵션: 없음 (기본 목록 조회만)
 * - 처리 내용: 시뮬레이션 내 모든 이미지 조회, 테이블 형태 출력
 * - 출력 형태: REPOSITORY, TAG, IMAGE ID, CREATED, SIZE 컬럼
 * - 예시: docker images, docker images nginx
 * 
 * 8. docker rmi [OPTIONS] IMAGE [IMAGE...]
 * - 이미지 삭제
 * - 지원 옵션: 없음 (기본 삭제 동작만)
 * - 처리 내용: 이미지 존재 확인, 데이터베이스에서 삭제
 * - 예시: docker rmi myapp:v1.0, docker rmi nginx:latest
 * 
 * 9. docker commit [OPTIONS] CONTAINER [REPOSITORY[:TAG]]
 * - 컨테이너를 이미지로 커밋 (현재 미구현)
 * - 상태: 향후 구현 예정
 * 
 * 교육적 특징:
 * - 실제 Docker와 유사한 출력 형태 제공
 * - 각 상황에 맞는 학습 힌트 제공
 * - 네임스페이스, 태그, 이미지 소스 등 핵심 개념 시뮬레이션
 * - 오류 상황에 대한 명확한 가이드 제공
 * - 단계별 빌드 과정과 Docker 워크플로우 학습 지원
 * 
 * 제한사항:
 * - 실제 컨테이너 실행 불가 (시뮬레이션만 제공)
 * - 실제 네트워크 통신 없음 (레지스트리 push/pull 시뮬레이션)
 * - 파일 시스템 변경 없음 (이미지 레이어는 메타데이터로만 관리)
 * - 일부 고급 옵션은 출력 메시지에만 반영 (실제 기능 구현 없음)
 * 
 * @author DockerSim Team
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ImageSimulationService {

    // Constants
    private static final String DEFAULT_TAG = "latest";
    private static final String ANONYMOUS_IMAGE_NAME = "<none>";
    private static final String ANONYMOUS_IMAGE_TAG = "<none>";
    private static final String LIBRARY_NAMESPACE = "library";
    private static final long SIMULATED_IMAGE_SIZE = 50_000_000L; // 50MB
    private static final int HASH_LENGTH = 12;

    // Error Messages
    private static final String ERROR_MISSING_ARGUMENTS = "Error: %s 명령어는 %s가 필요합니다.";
    private static final String ERROR_IMAGE_NOT_FOUND = "Error: 이미지를 찾을 수 없습니다: %s";
    private static final String ERROR_NAMESPACE_REQUIRED = "Error: 네임스페이스가 없는 이미지는 push할 수 없습니다: %s";

    // Dependencies
    private final ImageSimulationRepository imageRepository;
    private final DockerHubOfficialImageService officialImageService;

    // Record classes for clean data transfer
    private record ImageNameInfo(String name, String tag) {
    }

    private record BuildOptions(String imageTag, boolean noCache, boolean pull) {
    }

    private record ImageTagInfo(String name, String tag) {
    }

    /**
     * Docker 이미지 명령어를 실행합니다.
     * 
     * @param command      파싱된 Docker 명령어 객체
     * @param simulationId 시뮬레이션 세션 ID
     * @return 명령어 실행 결과
     */
    public CommandExecuteResponse executeCommand(ParsedDockerCommand command, String simulationId) {
        String subCommand = command.getSubCommand();

        return switch (subCommand) {
            case "pull" -> handleImagePull(command, simulationId);
            case "build" -> handleImageBuild(command, simulationId);
            case "tag" -> handleImageTag(command, simulationId);
            case "push" -> handleImagePush(command, simulationId);
            case "inspect" -> handleImageInspect(command, simulationId);
            case "prune" -> handleImagePrune(command, simulationId);
            case "commit" -> handleImageCommit(command, simulationId);
            case "images" -> handleImageList(command, simulationId);
            case "rmi" -> handleImageRemove(command, simulationId);
            default -> CommandExecuteResponse.builder()
                    .output("이미지 명령어 '" + subCommand + "'가 실행되었습니다. [시뮬레이션]")
                    .success(true)
                    .build();
        };
    }

    /**
     * Docker pull 명령어를 처리합니다.
     * 공식 이미지와 비공식 이미지를 구분하여 검증하고 다운로드를 시뮬레이션합니다.
     * 
     * @param command      파싱된 pull 명령어
     * @param simulationId 시뮬레이션 세션 ID
     * @return 실행 결과
     */
    private CommandExecuteResponse handleImagePull(ParsedDockerCommand command, String simulationId) {
        String imageName = command.getArguments().get(0);
        ImageNameInfo imageInfo = parseImageName(imageName);

        // 공식 이미지 검증
        DockerHubOfficialImageService.ImageValidationResult validation = officialImageService
                .validateImagePull(imageInfo.name(), imageInfo.tag());

        if (!validation.isValid()) {
            return createErrorResponse(validation.getMessage(), getImagePullHint(imageInfo.name()));
        }

        // 검증을 통과한 경우에만 이미지 생성
        ensureImageExists(simulationId, imageInfo.name(), imageInfo.tag(), validation.isOfficial());

        String hint = validation.isOfficial()
                ? "공식 이미지가 성공적으로 다운로드되었습니다. 'docker images'로 확인할 수 있습니다."
                : "비공식 이미지가 성공적으로 다운로드되었습니다. 'docker images'로 확인할 수 있습니다.";

        return CommandExecuteResponse.builder()
                .output(validation.getMessage())
                .success(true)
                .hint(hint)
                .build();
    }

    /**
     * Docker build 명령어를 처리합니다.
     * Dockerfile을 이용한 이미지 빌드를 시뮬레이션하며, 다양한 빌드 옵션을 지원합니다.
     * 
     * @param command      파싱된 build 명령어
     * @param simulationId 시뮬레이션 세션 ID
     * @return 실행 결과
     */
    private CommandExecuteResponse handleImageBuild(ParsedDockerCommand command, String simulationId) {
        BuildOptions buildOptions = extractBuildOptions(command);
        StringBuilder output = new StringBuilder();

        // 빌드 과정 시뮬레이션
        simulateBuildProcess(output);

        String finalImageId = generateImageId();
        ImageTagInfo tagInfo = processBuildTag(buildOptions.imageTag(), output, finalImageId);

        // 동일한 이미지명이 이미 존재하는 경우 덮어쓰기
        if (!ANONYMOUS_IMAGE_NAME.equals(tagInfo.name())) {
            handleImageOverwrite(simulationId, tagInfo.name(), tagInfo.tag(), output);
        }

        // 빌드 옵션 메시지 추가
        appendBuildOptionsMessages(output, buildOptions);

        // 새 이미지 생성
        createBuiltImage(simulationId, tagInfo.name(), tagInfo.tag());

        return CommandExecuteResponse.builder()
                .output(output.toString())
                .success(true)
                .hint(generateBuildHint(buildOptions.imageTag()))
                .build();
    }

    private CommandExecuteResponse handleImageCommit(ParsedDockerCommand command, String simulationId) {
        return null;
    }

    private CommandExecuteResponse handleImageList(ParsedDockerCommand command, String simulationId) {
        List<ImageSimulation> images = imageRepository.findBySimulationId(simulationId);

        StringBuilder output = new StringBuilder();
        output.append("REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE\n");

        for (ImageSimulation image : images) {
            String displayName = image.getName();
            if (image.getNamespace() != null && !image.getNamespace().isEmpty()) {
                displayName = image.getNamespace() + "/" + image.getName();
            }

            output.append(String.format("%-20s %-20s %-20s %-20s %-20s\n",
                    displayName,
                    image.getTag(),
                    image.getId().toString().substring(0, Math.min(12, image.getId().toString().length())),
                    "About a minute ago",
                    "N/A"));
        }

        return CommandExecuteResponse.builder()
                .output(output.toString())
                .success(true)
                .build();
    }

    private CommandExecuteResponse handleImageRemove(ParsedDockerCommand command, String simulationId) {
        String imageName = command.getArguments().get(0);
        String[] parts = imageName.split(":");
        String name = parts[0];
        String tag = parts.length > 1 ? parts[1] : "latest";

        // 네임스페이스가 있는 경우 실제 이미지명만 추출
        String actualImageName = officialImageService.extractImageName(name);

        Optional<ImageSimulation> imageOpt = imageRepository.findBySimulationIdAndNameAndTag(simulationId,
                actualImageName, tag);

        if (!imageOpt.isPresent()) {
            return CommandExecuteResponse.builder()
                    .output("Error: 이미지를 찾을 수 없습니다: " + imageName)
                    .success(false)
                    .build();
        }

        imageRepository.delete(imageOpt.get());

        return CommandExecuteResponse.builder()
                .output("이미지 '" + imageName + "'를 삭제했습니다.")
                .success(true)
                .build();
    }

    public void ensureImageExists(String simulationId, String imageName, String imageTag) {
        // 기존 메서드 - 이전 버전과의 호환성을 위해 유지
        ensureImageExists(simulationId, imageName, imageTag, true);
    }

    public void ensureImageExists(String simulationId, String fullImageName, String imageTag, boolean isOfficial) {
        String actualImageName = officialImageService.extractImageName(fullImageName);
        String namespace = officialImageService.extractNamespace(fullImageName);

        Optional<ImageSimulation> existingImage = imageRepository.findBySimulationIdAndNameAndTag(simulationId,
                actualImageName, imageTag);

        if (!existingImage.isPresent()) {
            ImageSimulation.ImageSimulationBuilder builder = ImageSimulation.builder()
                    .simulationId(simulationId)
                    .name(actualImageName)
                    .tag(imageTag)
                    .source(ImageSource.PULLED);

            // 공식 이미지의 경우 namespace를 "library"로 설정
            if (isOfficial && namespace == null) {
                builder.namespace("library");
            } else if (namespace != null) {
                builder.namespace(namespace);
            }

            imageRepository.save(builder.build());

            log.info("이미지 생성됨: {}/{} (공식: {})",
                    isOfficial && namespace == null ? "library" : namespace, actualImageName, isOfficial);
        }
    }

    public List<ImageSimulationDto> getCurrentImages(String simulationId) {
        return imageRepository.findBySimulationId(simulationId)
                .stream()
                .map(ImageSimulationDto::from)
                .toList();
    }

    private String getImagePullHint(String imageName) {
        if (officialImageService.hasNamespace(imageName)) {
            return "네임스페이스가 포함된 이미지는 비공식 이미지입니다.";
        }
        return "공식 이미지가 아닌 경우 'username/imagename' 형태로 네임스페이스를 포함해야 합니다.";
    }

    /**
     * 동일한 이미지명이 존재하는 경우 덮어쓰기 처리
     */
    private void handleImageOverwrite(String simulationId, String imageName, String tag, StringBuilder output) {
        Optional<ImageSimulation> existingImage = imageRepository.findBySimulationIdAndNameAndTag(simulationId,
                imageName, tag);

        if (existingImage.isPresent()) {
            imageRepository.delete(existingImage.get());
            output.append("기존 이미지 '").append(imageName).append(":").append(tag).append("'를 덮어씁니다.\n");
            log.info("기존 이미지 덮어쓰기: {}:{} (simulationId: {})", imageName, tag, simulationId);
        }
    }

    /**
     * 빌드된 이미지를 데이터베이스에 저장
     */
    private void createBuiltImage(String simulationId, String imageName, String tag) {
        String actualImageName = officialImageService.extractImageName(imageName);
        String namespace = officialImageService.extractNamespace(imageName);

        ImageSimulation.ImageSimulationBuilder builder = ImageSimulation.builder()
                .simulationId(simulationId)
                .name(actualImageName)
                .tag(tag)
                .source(ImageSource.BUILT);

        if (namespace != null) {
            builder.namespace(namespace);
        }

        imageRepository.save(builder.build());
        log.info("빌드된 이미지 저장됨: {}:{} (simulationId: {})", imageName, tag, simulationId);
    }

    /**
     * 빌드 명령어에 대한 힌트 생성
     */
    private String generateBuildHint(String imageTag) {
        if (imageTag == null || imageTag.isEmpty()) {
            return "이미지에 태그를 지정하지 않으면 익명 이미지가 생성됩니다. " +
                    "태그를 지정하려면 '-t' 옵션을 사용하세요. 예: docker build -t myapp:latest .";
        }
        return "이미지가 성공적으로 빌드되었습니다. 'docker images'로 생성된 이미지를 확인할 수 있습니다.";
    }

    /**
     * Docker tag 명령어를 처리합니다.
     * 기존 이미지에 새로운 태그를 생성합니다.
     * 
     * @param command      파싱된 tag 명령어
     * @param simulationId 시뮬레이션 세션 ID
     * @return 실행 결과
     */
    private CommandExecuteResponse handleImageTag(ParsedDockerCommand command, String simulationId) {
        if (command.getArguments().size() < 2) {
            return createErrorResponse(
                    String.format(ERROR_MISSING_ARGUMENTS, "docker tag", "SOURCE_IMAGE와 TARGET_IMAGE"),
                    "사용법: docker tag SOURCE_IMAGE[:TAG] TARGET_IMAGE[:TAG]");
        }

        String sourceImage = command.getArguments().get(0);
        String targetImage = command.getArguments().get(1);

        ImageNameInfo sourceInfo = parseImageName(sourceImage);
        ImageNameInfo targetInfo = parseImageName(targetImage);

        // 소스 이미지 존재 확인
        String actualSourceName = officialImageService.extractImageName(sourceInfo.name());
        Optional<ImageSimulation> sourceImageOpt = imageRepository.findBySimulationIdAndNameAndTag(
                simulationId, actualSourceName, sourceInfo.tag());

        if (!sourceImageOpt.isPresent()) {
            return createErrorResponse(String.format(ERROR_IMAGE_NOT_FOUND, sourceImage), null);
        }

        // 타겟 이미지 생성
        return createTaggedImage(simulationId, sourceImageOpt.get(), targetInfo, targetImage);
    }

    /**
     * Docker push 명령어를 처리합니다.
     * 네임스페이스가 있는 이미지를 레지스트리에 업로드합니다.
     * 
     * @param command      파싱된 push 명령어
     * @param simulationId 시뮬레이션 세션 ID
     * @return 실행 결과
     */
    private CommandExecuteResponse handleImagePush(ParsedDockerCommand command, String simulationId) {
        if (command.getArguments().isEmpty()) {
            return createErrorResponse(
                    String.format(ERROR_MISSING_ARGUMENTS, "docker push", "이미지명"),
                    "사용법: docker push [OPTIONS] NAME[:TAG]");
        }

        String imageName = command.getArguments().get(0);
        ImageNameInfo imageInfo = parseImageName(imageName);

        // 네임스페이스 검증 - push는 네임스페이스가 필수
        if (!officialImageService.hasNamespace(imageInfo.name())) {
            String hint = String.format(
                    "이미지를 push하려면 'username/imagename' 형태로 네임스페이스를 포함해야 합니다. " +
                            "'docker tag %s username/%s:%s'로 태그를 먼저 생성하세요.",
                    imageName, imageInfo.name(), imageInfo.tag());
            return createErrorResponse(String.format(ERROR_NAMESPACE_REQUIRED, imageName), hint);
        }

        // 이미지 존재 확인 - 네임스페이스가 있는 경우 전체 이름으로 검색
        String searchImageName = imageInfo.name();
        String actualImageName = officialImageService.extractImageName(imageInfo.name());

        // 먼저 전체 이름으로 검색 시도
        Optional<ImageSimulation> imageOpt = imageRepository.findBySimulationIdAndNameAndTag(
                simulationId, searchImageName, imageInfo.tag());

        // 전체 이름으로 찾지 못하면 실제 이미지명으로 검색 시도 (호환성)
        if (!imageOpt.isPresent()) {
            imageOpt = imageRepository.findBySimulationIdAndNameAndTag(
                    simulationId, actualImageName, imageInfo.tag());
        }

        if (!imageOpt.isPresent()) {
            return createErrorResponse(String.format(ERROR_IMAGE_NOT_FOUND, imageName), null);
        }

        return simulatePushProcess(imageName, imageInfo.tag());
    }

    /**
     * Docker inspect 명령어를 처리합니다.
     * 이미지의 상세 정보를 JSON 형태로 출력합니다.
     * 
     * @param command      파싱된 inspect 명령어
     * @param simulationId 시뮬레이션 세션 ID
     * @return 실행 결과
     */
    private CommandExecuteResponse handleImageInspect(ParsedDockerCommand command, String simulationId) {
        if (command.getArguments().isEmpty()) {
            return createErrorResponse(
                    String.format(ERROR_MISSING_ARGUMENTS, "docker inspect", "이미지명"),
                    "사용법: docker inspect IMAGE[:TAG]");
        }

        String imageName = command.getArguments().get(0);
        ImageNameInfo imageInfo = parseImageName(imageName);

        // 이미지 존재 확인
        String actualImageName = officialImageService.extractImageName(imageInfo.name());
        Optional<ImageSimulation> imageOpt = imageRepository.findBySimulationIdAndNameAndTag(
                simulationId, actualImageName, imageInfo.tag());

        if (!imageOpt.isPresent()) {
            return createErrorResponse(String.format(ERROR_IMAGE_NOT_FOUND, imageName), null);
        }

        return generateInspectOutput(imageOpt.get(), imageName);
    }

    /**
     * Docker image prune 명령어를 처리합니다.
     * 불필요한 이미지들을 정리합니다.
     * 
     * @param command      파싱된 prune 명령어
     * @param simulationId 시뮬레이션 세션 ID
     * @return 실행 결과
     */
    private CommandExecuteResponse handleImagePrune(ParsedDockerCommand command, String simulationId) {
        boolean all = command.getOptions().containsKey("-a") || command.getOptions().containsKey("--all") ||
                command.getFlags().contains("-a") || command.getFlags().contains("--all");
        boolean force = command.getOptions().containsKey("-f") || command.getOptions().containsKey("--force") ||
                command.getFlags().contains("-f") || command.getFlags().contains("--force");

        List<ImageSimulation> imagesToRemove = findImagesToPrune(simulationId, all);

        if (imagesToRemove.isEmpty()) {
            String hint = all ? "제거할 이미지가 없습니다." : "제거할 dangling 이미지가 없습니다. 모든 이미지를 제거하려면 '-a' 옵션을 사용하세요.";
            return CommandExecuteResponse.builder()
                    .output("Total reclaimed space: 0B")
                    .success(true)
                    .hint(hint)
                    .build();
        }

        if (!force) {
            return generatePruneWarning(imagesToRemove, all);
        }

        return executePrune(imagesToRemove);
    }

    // ===== Helper Methods =====

    /**
     * 이미지명을 파싱하여 이름과 태그로 분리합니다.
     */
    private ImageNameInfo parseImageName(String imageName) {
        String[] parts = imageName.split(":");
        String name = parts[0];
        String tag = parts.length > 1 ? parts[1] : DEFAULT_TAG;
        return new ImageNameInfo(name, tag);
    }

    /**
     * 에러 응답을 생성합니다.
     */
    private CommandExecuteResponse createErrorResponse(String message, String hint) {
        return CommandExecuteResponse.builder()
                .output("Error: " + message)
                .success(false)
                .hint(hint)
                .build();
    }

    /**
     * 빌드 옵션을 추출합니다.
     */
    private BuildOptions extractBuildOptions(ParsedDockerCommand command) {
        String imageTag = command.getOptions().get("-t");
        if (imageTag == null) {
            imageTag = command.getOptions().get("--tag");
        }

        boolean noCache = command.getOptions().containsKey("--no-cache");
        boolean pull = command.getOptions().containsKey("--pull");

        return new BuildOptions(imageTag, noCache, pull);
    }

    /**
     * 빌드 과정을 시뮬레이션합니다.
     */
    private void simulateBuildProcess(StringBuilder output) {
        output.append("Sending build context to Docker daemon...\n");
        output.append("Building image from Dockerfile...\n");
    }

    /**
     * 가상의 이미지 ID를 생성합니다.
     */
    private String generateImageId() {
        return "sha256:abc123def456" + System.currentTimeMillis();
    }

    /**
     * 빌드 태그를 처리합니다.
     */
    private ImageTagInfo processBuildTag(String imageTag, StringBuilder output, String finalImageId) {
        String imageName;
        String tag;

        if (imageTag != null && !imageTag.isEmpty()) {
            // -t 옵션으로 태그가 지정된 경우
            String[] parts = imageTag.split(":");
            imageName = parts[0];
            tag = parts.length > 1 ? parts[1] : DEFAULT_TAG;

            output.append("Successfully built ").append(finalImageId.substring(7, 7 + HASH_LENGTH)).append("\n");
            output.append("Successfully tagged ").append(imageTag).append("\n");

        } else {
            // 익명 이미지 생성 (태그 없이)
            imageName = ANONYMOUS_IMAGE_NAME;
            tag = ANONYMOUS_IMAGE_TAG;
            output.append("Successfully built ").append(finalImageId.substring(7, 7 + HASH_LENGTH)).append("\n");
            output.append("Warning: 이미지에 태그가 지정되지 않았습니다. 익명 이미지가 생성됩니다.\n");
        }

        return new ImageTagInfo(imageName, tag);
    }

    /**
     * 빌드 옵션 관련 메시지를 추가합니다.
     */
    private void appendBuildOptionsMessages(StringBuilder output, BuildOptions buildOptions) {
        if (buildOptions.noCache()) {
            output.append("Cache disabled for this build\n");
        }
        if (buildOptions.pull()) {
            output.append("Pulling base image updates\n");
        }
    }

    /**
     * 태그된 이미지를 생성합니다.
     */
    private CommandExecuteResponse createTaggedImage(String simulationId, ImageSimulation sourceImage,
            ImageNameInfo targetInfo, String targetImage) {
        String targetActualName = officialImageService.extractImageName(targetInfo.name());
        String targetNamespace = officialImageService.extractNamespace(targetInfo.name());

        // 기존 타겟 이미지가 있으면 덮어쓰기
        handleImageOverwrite(simulationId, targetActualName, targetInfo.tag(), new StringBuilder());

        ImageSimulation.ImageSimulationBuilder builder = ImageSimulation.builder()
                .simulationId(simulationId)
                .name(targetActualName)
                .tag(targetInfo.tag())
                .source(sourceImage.getSource());

        if (targetNamespace != null) {
            builder.namespace(targetNamespace);
        }

        imageRepository.save(builder.build());

        return CommandExecuteResponse.builder()
                .output("이미지 태그가 생성되었습니다: " + targetImage)
                .success(true)
                .hint("'docker images'로 새로 생성된 태그를 확인할 수 있습니다.")
                .build();
    }

    /**
     * push 과정을 시뮬레이션합니다.
     */
    private CommandExecuteResponse simulatePushProcess(String imageName, String tag) {
        StringBuilder output = new StringBuilder();
        output.append("The push refers to repository [").append(imageName).append("]\n");
        output.append("Pushing to registry...\n");
        output.append(tag).append(": Pushed\n");
        output.append(imageName).append(": digest: sha256:abc123def456... size: 1234\n");

        return CommandExecuteResponse.builder()
                .output(output.toString())
                .success(true)
                .hint("이미지가 성공적으로 레지스트리에 push되었습니다.")
                .build();
    }

    /**
     * inspect 출력을 생성합니다.
     */
    private CommandExecuteResponse generateInspectOutput(ImageSimulation image, String imageName) {
        StringBuilder output = new StringBuilder();
        output.append("[\n");
        output.append("    {\n");
        output.append("        \"Id\": \"sha256:").append(image.getId()).append("\",\n");
        output.append("        \"RepoTags\": [\n");
        output.append("            \"").append(imageName).append("\"\n");
        output.append("        ],\n");
        output.append("        \"Created\": \"").append(image.getCreatedAt()).append("\",\n");
        output.append("        \"Size\": ").append(SIMULATED_IMAGE_SIZE).append(",\n");
        output.append("        \"Architecture\": \"amd64\",\n");
        output.append("        \"Os\": \"linux\",\n");
        output.append("        \"Config\": {\n");
        output.append("            \"ExposedPorts\": {\n");
        output.append("                \"8080/tcp\": {}\n");
        output.append("            },\n");
        output.append("            \"Env\": [\n");
        output.append("                \"PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin\",\n");
        output.append("                \"JAVA_HOME=/usr/local/openjdk-11\"\n");
        output.append("            ]\n");
        output.append("        }\n");
        output.append("    }\n");
        output.append("]\n");

        return CommandExecuteResponse.builder()
                .output(output.toString())
                .success(true)
                .build();
    }

    /**
     * 정리할 이미지들을 찾습니다.
     */
    private List<ImageSimulation> findImagesToPrune(String simulationId, boolean all) {
        if (all) {
            return imageRepository.findBySimulationId(simulationId);
        } else {
            return imageRepository.findBySimulationId(simulationId)
                    .stream()
                    .filter(img -> ANONYMOUS_IMAGE_NAME.equals(img.getName())
                            && ANONYMOUS_IMAGE_TAG.equals(img.getTag()))
                    .toList();
        }
    }

    /**
     * prune 경고 메시지를 생성합니다.
     */
    private CommandExecuteResponse generatePruneWarning(List<ImageSimulation> imagesToRemove, boolean all) {
        StringBuilder output = new StringBuilder();
        output.append("WARNING! This will remove ");
        output.append(all ? "all images" : "all dangling images");
        output.append(":\n");

        for (ImageSimulation image : imagesToRemove) {
            String displayName = buildDisplayName(image);
            output.append("  ").append(displayName).append(":").append(image.getTag()).append("\n");
        }

        output.append("\nAre you sure you want to continue? [y/N] ");
        output.append("(시뮬레이션에서는 자동으로 취소됩니다. '-f' 옵션을 사용하여 강제 실행하세요.)");

        return CommandExecuteResponse.builder()
                .output(output.toString())
                .success(false)
                .hint("'-f' 또는 '--force' 옵션을 사용하여 확인 없이 삭제할 수 있습니다.")
                .build();
    }

    /**
     * prune을 실행합니다.
     */
    private CommandExecuteResponse executePrune(List<ImageSimulation> imagesToRemove) {
        long totalSize = 0;
        StringBuilder output = new StringBuilder();
        output.append("Deleted Images:\n");

        for (ImageSimulation image : imagesToRemove) {
            output.append("deleted: sha256:").append(image.getId()).append("\n");
            totalSize += SIMULATED_IMAGE_SIZE;
            imageRepository.delete(image);
        }

        output.append("\nTotal reclaimed space: ").append(totalSize / 1_000_000).append("MB");

        return CommandExecuteResponse.builder()
                .output(output.toString())
                .success(true)
                .hint(imagesToRemove.size() + "개의 이미지가 삭제되었습니다.")
                .build();
    }

    /**
     * 이미지의 표시명을 생성합니다.
     */
    private String buildDisplayName(ImageSimulation image) {
        String displayName = image.getName();
        if (image.getNamespace() != null && !image.getNamespace().isEmpty()) {
            displayName = image.getNamespace() + "/" + image.getName();
        }
        return displayName;
    }

    /**
     * 컨테이너로부터 이미지 생성 (docker commit)
     */
    public CommandExecuteResponse createImageFromContainer(String simulationId, Object container,
            String imageName, String tag, String message) {
        try {
            // 새 이미지 생성
            String actualImageName = officialImageService.extractImageName(imageName);
            String namespace = officialImageService.extractNamespace(imageName);

            ImageSimulation.ImageSimulationBuilder builder = ImageSimulation.builder()
                    .simulationId(simulationId)
                    .name(actualImageName)
                    .tag(tag != null ? tag : "latest")
                    .source(ImageSource.COMMITTED);

            if (namespace != null) {
                builder.namespace(namespace);
            }

            ImageSimulation newImage = builder.build();

            // 기존 이미지가 있으면 덮어쓰기
            Optional<ImageSimulation> existingImage = imageRepository.findBySimulationIdAndNameAndTag(
                    simulationId, actualImageName, newImage.getTag());

            if (existingImage.isPresent()) {
                imageRepository.delete(existingImage.get());
                log.info("기존 이미지 덮어쓰기: {}:{} (simulationId: {})",
                        actualImageName, newImage.getTag(), simulationId);
            }

            imageRepository.save(newImage);
            log.info("컨테이너로부터 이미지 생성됨: {}:{} (simulationId: {})",
                    actualImageName, newImage.getTag(), simulationId);

            return CommandExecuteResponse.builder()
                    .success(true)
                    .stateChanges(Map.of("created", List.of(ImageSimulationDto.from(newImage))))
                    .build();

        } catch (Exception e) {
            log.error("컨테이너로부터 이미지 생성 중 오류: {}", e.getMessage(), e);
            return createErrorResponse("컨테이너로부터 이미지 생성 중 오류가 발생했습니다: " + e.getMessage(), null);
        }
    }

}