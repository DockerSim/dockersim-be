package com.dockersim.service.image;

import com.dockersim.config.SimulationUserPrincipal;
import com.dockersim.domain.*;
import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.dto.response.ImageInspectData;
import com.dockersim.dto.response.ImageInspectRootFSData;
import com.dockersim.dto.util.ImageMeta;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerImageErrorCode;
import com.dockersim.repository.DockerImageRepository;
import com.dockersim.service.dockerfile.DockerFileFinder;
import com.dockersim.service.simulation.SimulationFinder;
import com.dockersim.service.user.UserFinder;
import com.dockersim.util.ImageUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class DockerImageServiceImpl implements DockerImageService {

    private final DockerImageRepository repo;

    private final SimulationFinder simulationFinder;
    private final DockerImageFinder dockerImageFinder;
    private final UserFinder userFinder;
    private final DockerFileFinder dockerFileFinder;
    private final DockerOfficeImageService dockerOfficeImageService; // Inject DockerOfficeImageService

    @Override
    public DockerImageResponse build(SimulationUserPrincipal principal, String dockerFilePath, String tag) {
        Simulation simulation = simulationFinder.findById(principal.getSimulationId());
        User user = userFinder.findUserById(principal.getUserId());
        DockerFile dockerFile = dockerFileFinder.findByPathAndUser(dockerFilePath, user);

        ImageMeta imageInfo = ImageUtil.parserFullName(tag);
        ImageUtil.checkInvalidImageInfo(imageInfo, user, true);

        DockerImage image = DockerImage.from(simulation, dockerFile, imageInfo);

        DockerImage prevImage = dockerImageFinder.findImageInLocalOrNull(simulation, imageInfo);
        if (prevImage != null) {
            prevImage.convertToDangling();
            repo.save(prevImage);
        }

        Stream<String> headerStream = Stream.of(
                dockerFilePath + "에 위치한 Dockerfile에 의해 Image" + image.getName() + "을 생성했습니다.");
        Stream<String> bodyStream = Stream.empty();

        if (prevImage != null) {
            bodyStream = Stream.of(
                    "Local에 존재하는 기존의 Image는 댕글링이미지로 변환됩니다.:",
                    prevImage.getShortHexId()
            );
        }

        return DockerImageResponse.from(repo.save(image),
                Stream.concat(headerStream, bodyStream).toList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> history(SimulationUserPrincipal principal, String nameOrHexId) {
        ImageMeta imageInfo = ImageUtil.parserFullName(nameOrHexId);

        Simulation simulation = simulationFinder.findById(principal.getSimulationId());

        DockerImage image = dockerImageFinder.findImage(simulation, imageInfo, ImageLocation.LOCAL);
        return image.getLayers();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> inspect(SimulationUserPrincipal principal, String nameOrHexId) {

        Simulation simulation = simulationFinder.findById(principal.getSimulationId());

        ImageMeta imageInfo = ImageUtil.parserFullName(nameOrHexId);
        DockerImage image = dockerImageFinder.findImageOrNull(simulation, imageInfo, ImageLocation.LOCAL);

        ImageInspectData inspectData = ImageInspectData.builder()
                .Id(image.getHexId())
                .RepoTags(List.of(image.getFullNameWithTag()))
                .Created(image.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME))
                .RootFS(ImageInspectRootFSData.builder().Layers(image.getLayers()).build())
                .build();

        try {
            return Collections.singletonList(
                    (new ObjectMapper()).writerWithDefaultPrettyPrinter().writeValueAsString(List.of(inspectData)));
        } catch (JsonProcessingException e) {
            throw new BusinessException(DockerImageErrorCode.FAIL_CONVERT_INSPECT);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> ls(SimulationUserPrincipal principal, boolean all, boolean quiet) {
        Simulation simulation = simulationFinder.findById(principal.getSimulationId());
        List<DockerImage> images = dockerImageFinder.findBySimulationInLocal(simulation, all);

        Stream<String> headerStream, bodyStream;
        if (quiet) {
            headerStream = Stream.of("REPOSITORY");
            bodyStream = images.stream().map(DockerImage::getHexId);
        } else {
            headerStream = String.format("%-25s %-20s %-15s %-20s", "REPOSITORY", "TAG", "IMAGE ID", "CREATED").lines();
            bodyStream = images.stream().map(
                    image ->
                            String.format(
                                    "%-25s %-20s %-15s %-20s",
                                    image.getName(),
                                    image.getTag(),
                                    image.getShortHexId(),
                                    formatDuration(image.getCreatedAt())
                            )
            );
        }
        return Stream.concat(headerStream, bodyStream).toList();
    }

    private String formatDuration(LocalDateTime time) {

        Duration duration = Duration.between(time, LocalDateTime.now());
        long days = duration.toDays();
        if (days > 0) {
            return days + (days == 1 ? " day ago" : " days ago");
        }
        long hours = duration.toHours();
        if (hours > 0) {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        }
        long minutes = duration.toMinutes();
        if (minutes > 0) {
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        }
        return "just now";
    }

    @Override
    public List<DockerImageResponse> prune(SimulationUserPrincipal principal, boolean all) {
        Simulation simulation = simulationFinder.findById(principal.getSimulationId());
        List<DockerImage> images;

        if (all) {
            images = dockerImageFinder.findDanglingImageBySimulationInLocal(simulation)
                    .stream().filter(image -> image.getContainers().isEmpty()).toList();
        } else {
            images = dockerImageFinder.findUnreferencedImageBySimulationInLocal(simulation);
        }

        return images.stream()
                .map(image -> DockerImageResponse.from(image, List.of("deleted: " + image.getHexId())))
                .toList();
    }

    @Override
    public List<DockerImageResponse> pull(SimulationUserPrincipal principal, String name, boolean allTags) {
        ImageMeta meta = ImageUtil.parserFullName(name);
        String namespace = meta.getNamespace();


        if (!namespace.isEmpty() && !namespace.equals("library")) {
            // 명시된 공식 이미지도 아니고, 네임스페이스가 비어있지도 않다면 사용자 허브에 저장된 이미지 중 조회
            // 네임스페이스에 사용자 이름 등록 필요
            User user = userFinder.findUserById(principal.getUserId());
            meta.updateNamespace(user.getName());
        } else {
            meta.updateNamespace("library");
        }

        Simulation simulation = simulationFinder.findById(principal.getSimulationId());
        List<DockerImage> images = dockerImageFinder.findPullImageByInfo(simulation, meta, allTags);

        // If no image found locally or in user's hub, try to pull from official images
        if (images.isEmpty()) {
            if (allTags) {
                // If allTags is true, pull all tags for the given image name from official images
                dockerOfficeImageService.findAllByName(meta.getName()).stream()
                        .map(officeImageResponse -> DockerImage.from(officeImageResponse, simulation, ImageLocation.LOCAL))
                        .forEach(images::add);
            } else {
                // Pull specific tag from official images
                dockerOfficeImageService.findByNameAndTag(meta.getName(), meta.getTag())
                        .ifPresent(officeImageResponse -> images.add(DockerImage.from(officeImageResponse, simulation, ImageLocation.LOCAL)));
            }
        }

        if (images.isEmpty()) {
            throw new BusinessException(DockerImageErrorCode.IMAGE_NOT_FOUND, name);
        }

        images.forEach(image -> image.addSimulation(simulation));
        repo.saveAll(images);

        return images.stream()
                .map(image -> DockerImageResponse.from(image, List.of("pulled: " + image.getShortHexId())))
                .toList();
    }

    @Override
    @Transactional
    public List<DockerImageResponse> push(SimulationUserPrincipal principal, String name, boolean allTags) {

        ImageMeta meta = ImageUtil.parserFullName(name);

		/*
		 1) 명시된 네임스페이스 검증
		 - 네임스페이스가 비어있지 않고, 사용자 닉네임과 일치하는지 확인
		 - 조직 네임스페이스는 고려하지 않는다.
		 */
        User user = userFinder.findUserById(principal.getUserId());
        if (!meta.getNamespace().equals(user.getName())) {
            throw new BusinessException(DockerImageErrorCode.INVALID_NAMESPACE, meta.getNamespace());
        }

        Simulation simulation = simulationFinder.findById(principal.getSimulationId());
		/*
		 2) Local에서 업로드할 Image 조회
		 - 태그명이 생략될 경우 latest를 붙인다.
		 - 업로드할 Image가 없다면 예외 발생
		 - all 활성화: Local에서 namespace와 name이 같은 모든 Image 조회
		 - all 비활성화: image map으로 단일 조회
		 */
        List<DockerImage> localImages = dockerImageFinder.findPushImageInLocal(simulation, meta, allTags);

		/*
		 3) localImage 중 Hub에 올라간 Image를 댕글링 이미지로 변환
		 - Hub에 동일한 Image가 존재한다면 기존 Image는 댕글링 이미지로 저장한다.
		 - localImages의 태그와 일치하는 이미지만 댕글링 이미지로 변환해야함
		 - all 활성화: Hub에서 localImage 중 namespace와 name이 같은 모든 Image 조회
		 - all 비활성화: image map으로 단일 조회
		 - 댕글링 이미지와 변환 안내 문구를 프론트 반환.
		 */
        List<DockerImage> prevImage = dockerImageFinder.findOldPushImageInHub(simulation, localImages, meta, allTags);

        Stream<DockerImageResponse> danglingStream = prevImage.stream().map(
                image -> {
                    String output = "dangling: " + image.getFullNameWithTag();
                    image.convertToDangling();
                    return DockerImageResponse.from(repo.save(image), List.of(output));
                }
        );

		/*
		 4) Local에서 조회한 Image를 Hub로 Push
		 - 조회한 Local Image의 정보 재활용
		 - location만 HUB로 변경한 새로운 Image 생성 및 저장
		 */
        Stream<DockerImageResponse> pushedStream = localImages.stream()
                .map(image -> DockerImage.from(image, ImageLocation.HUB))
                .map(newImage -> DockerImageResponse.from(
                                repo.save(newImage),
                                List.of("pushed: " + newImage.getShortHexId())
                        )
                );

        return Stream.concat(danglingStream, pushedStream).toList();
    }

    @Override
    public DockerImageResponse rm(SimulationUserPrincipal principal, String nameOrId, boolean force) {
        Simulation simulation = simulationFinder.findById(principal.getSimulationId());
        ImageMeta imageMeta = ImageUtil.parserFullName(nameOrId);

        DockerImage image = dockerImageFinder.findImageByNameBeforeShortHexId(simulation, imageMeta,
                ImageLocation.LOCAL);
        if (force || image.getContainers().isEmpty()) {
            repo.delete(image);
            return DockerImageResponse.from(image, List.of("deleted: " + image.getHexId()));
        } else {
            throw new BusinessException(DockerImageErrorCode.FAIL_REMOVE_BASE_IMAGE, nameOrId);
        }
    }
}
