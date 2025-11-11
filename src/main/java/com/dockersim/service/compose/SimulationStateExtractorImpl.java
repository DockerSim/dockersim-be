package com.dockersim.service.compose;

import com.dockersim.context.SimulationContextHolder;
import com.dockersim.domain.*;
import com.dockersim.dto.request.ComposeGenerationRequest.ContainerInfo;
import com.dockersim.dto.request.ComposeGenerationRequest.ImageInfo;
import com.dockersim.dto.request.ComposeGenerationRequest.InfrastructureData;
import com.dockersim.dto.request.ComposeGenerationRequest.NetworkInfo;
import com.dockersim.dto.request.ComposeGenerationRequest.VolumeInfo;
import com.dockersim.service.container.DockerContainerFinder;
import com.dockersim.service.image.DockerImageFinder;
import com.dockersim.service.network.DockerNetworkFinder;
import com.dockersim.service.simulation.SimulationFinder;
import com.dockersim.service.volume.DockerVolumeFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 시뮬레이션의 현재 Docker 상태를 추출하는 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SimulationStateExtractorImpl implements SimulationStateExtractor {

    private final SimulationFinder simulationFinder;
    private final DockerContainerFinder containerFinder;
    private final DockerImageFinder imageFinder;
    private final DockerNetworkFinder networkFinder;
    private final DockerVolumeFinder volumeFinder;

    @Override
    public InfrastructureData extractInfrastructureData(String simulationPublicId) {
        log.info("시뮬레이션 상태 추출 시작: simulationPublicId={}", simulationPublicId);

        // 시뮬레이션 조회
        Simulation simulation = simulationFinder.findByPublicId(simulationPublicId);

        // 시뮬레이션 컨텍스트 설정
        SimulationContextHolder.setSimulationId(simulationPublicId);

        try {
            // 현재 시뮬레이션의 Docker 상태 정보 수집
            List<ContainerInfo> containers = extractContainers(simulation);
            List<ImageInfo> images = extractImages(simulation);
            List<NetworkInfo> networks = extractNetworks(simulation);
            List<VolumeInfo> volumes = extractVolumes(simulation);

            log.info("시뮬레이션 상태 추출 완료: containers={}, images={}, networks={}, volumes={}",
                containers.size(), images.size(), networks.size(), volumes.size());

            return InfrastructureData.builder()
                .containers(containers)
                .images(images)
                .networks(networks)
                .volumes(volumes)
                .build();

        } finally {
            // 컨텍스트 정리
            SimulationContextHolder.clear();
        }
    }

    private List<ContainerInfo> extractContainers(Simulation simulation) {
        try {
            log.debug("컨테이너 정보 추출 중 (DB)...");

            List<DockerContainer> containers = containerFinder.findBySimulation(simulation, true);

            List<ContainerInfo> result = containers.stream()
                .map(container -> {
                    // 볼륨 매핑 정보 추출
                    List<String> volumes = container.getContainerVolumes().stream()
                        .map(cv -> cv.getVolume().getName() + ":" + cv.getContainerPath() +
                            (cv.isReadOnly() ? ":ro" : ""))
                        .collect(Collectors.toList());

                    // 네트워크 정보 추출 (첫 번째 네트워크 이름)
                    String networkMode = container.getContainerNetworks().stream()
                        .findFirst()
                        .map(cn -> cn.getNetwork().getName())
                        .orElse("bridge");

                    return ContainerInfo.builder()
                        .name(container.getName())
                        .image(container.getBaseImage().getFullNameWithTag())
                        .ports(new ArrayList<>()) // 포트는 DB에 저장 안 됨
                        .volumes(volumes)
                        .environment(new ArrayList<>()) // 환경 변수는 DB에 저장 안 됨
                        .networkMode(networkMode)
                        .status(container.getStatus().name().toLowerCase())
                        .build();
                })
                .collect(Collectors.toList());

            log.debug("컨테이너 정보 추출 완료: {} 개", result.size());
            return result;

        } catch (Exception e) {
            log.warn("컨테이너 정보 추출 실패: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<ImageInfo> extractImages(Simulation simulation) {
        try {
            log.debug("이미지 정보 추출 중 (DB)...");

            List<DockerImage> images = imageFinder.findBySimulationInLocal(simulation, false);

            List<ImageInfo> result = images.stream()
                .map(image -> ImageInfo.builder()
                    .name(image.getName())
                    .tag(image.getTag())
                    .size(calculateImageSize(image))
                    .build())
                .collect(Collectors.toList());

            log.debug("이미지 정보 추출 완료: {} 개", result.size());
            return result;

        } catch (Exception e) {
            log.warn("이미지 정보 추출 실패: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private String calculateImageSize(DockerImage image) {
        // layers가 있으면 대략적인 크기 계산, 없으면 기본값
        if (image.getLayers() != null && !image.getLayers().isEmpty()) {
            return (image.getLayers().size() * 10) + "MB";
        }
        return "Unknown";
    }

    private List<NetworkInfo> extractNetworks(Simulation simulation) {
        try {
            log.debug("네트워크 정보 추출 중 (DB)...");

            List<DockerNetwork> networks = networkFinder.findAll(simulation);

            List<NetworkInfo> result = networks.stream()
                .map(network -> NetworkInfo.builder()
                    .name(network.getName())
                    .driver("bridge") // DB에 저장 안 됨, 기본값 사용
                    .subnet(null) // DB에 저장 안 됨
                    .build())
                .collect(Collectors.toList());

            log.debug("네트워크 정보 추출 완료: {} 개", result.size());
            return result;

        } catch (Exception e) {
            log.warn("네트워크 정보 추출 실패: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<VolumeInfo> extractVolumes(Simulation simulation) {
        try {
            log.debug("볼륨 정보 추출 중 (DB)...");

            List<DockerVolume> volumes = volumeFinder.findBySimulation(simulation);

            List<VolumeInfo> result = volumes.stream()
                .map(volume -> VolumeInfo.builder()
                    .name(volume.getName())
                    .driver("local") // DB에 저장 안 됨, 기본값 사용
                    .mountpoint(volume.getMountPoint())
                    .build())
                .collect(Collectors.toList());

            log.debug("볼륨 정보 추출 완료: {} 개", result.size());
            return result;

        } catch (Exception e) {
            log.warn("볼륨 정보 추출 실패: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}