package com.dockersim.service.compose;

import com.dockersim.context.SimulationContextHolder;
import com.dockersim.dto.request.ComposeGenerationRequest.ContainerInfo;
import com.dockersim.dto.request.ComposeGenerationRequest.ImageInfo;
import com.dockersim.dto.request.ComposeGenerationRequest.InfrastructureData;
import com.dockersim.dto.request.ComposeGenerationRequest.NetworkInfo;
import com.dockersim.dto.request.ComposeGenerationRequest.VolumeInfo;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.service.command.CommandExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 시뮬레이션의 현재 Docker 상태를 추출하는 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SimulationStateExtractorImpl implements SimulationStateExtractor {

    private final CommandExecutorService commandExecutorService;

    @Override
    public InfrastructureData extractInfrastructureData(String simulationPublicId) {
        log.info("시뮬레이션 상태 추출 시작: simulationPublicId={}", simulationPublicId);

        // 시뮬레이션 컨텍스트 설정
        SimulationContextHolder.setSimulationId(simulationPublicId);
        
        try {
            // 현재 시뮬레이션의 Docker 상태 정보 수집
            List<ContainerInfo> containers = extractContainers();
            List<ImageInfo> images = extractImages();
            List<NetworkInfo> networks = extractNetworks();
            List<VolumeInfo> volumes = extractVolumes();

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

    private List<ContainerInfo> extractContainers() {
        try {
            log.debug("컨테이너 정보 추출 중 (더미 데이터)...");
            
            List<ContainerInfo> containers = new ArrayList<>();
            
            // 더미 컨테이너 데이터 생성
            containers.add(ContainerInfo.builder()
                .name("nginx-web-server")
                .image("nginx:latest")
                .ports(Arrays.asList("80:80", "443:443"))
                .volumes(Arrays.asList("./html:/usr/share/nginx/html:ro"))
                .environment(Arrays.asList("NGINX_HOST=localhost", "NGINX_PORT=80"))
                .networkMode("bridge")
                .status("running")
                .build());
                
            containers.add(ContainerInfo.builder()
                .name("redis-cache")
                .image("redis:7-alpine")
                .ports(Arrays.asList("6379:6379"))
                .volumes(Arrays.asList("redis-data:/data"))
                .environment(Arrays.asList("REDIS_PASSWORD=secret"))
                .networkMode("app-network")
                .status("running")
                .build());
                
            containers.add(ContainerInfo.builder()
                .name("postgres-db")
                .image("postgres:15")
                .ports(Arrays.asList("5432:5432"))
                .volumes(Arrays.asList("postgres-data:/var/lib/postgresql/data"))
                .environment(Arrays.asList(
                    "POSTGRES_DB=myapp", 
                    "POSTGRES_USER=admin", 
                    "POSTGRES_PASSWORD=password123"
                ))
                .networkMode("app-network")
                .status("running")
                .build());
            
            log.debug("더미 컨테이너 정보 생성 완료: {} 개", containers.size());
            return containers;
            
        } catch (Exception e) {
            log.warn("컨테이너 정보 추출 실패: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<ImageInfo> extractImages() {
        try {
            log.debug("이미지 정보 추출 중 (더미 데이터)...");
            
            List<ImageInfo> images = new ArrayList<>();
            
            // 더미 이미지 데이터 생성
            images.add(ImageInfo.builder()
                .name("nginx")
                .tag("latest")
                .size("142MB")
                .build());
                
            images.add(ImageInfo.builder()
                .name("redis")
                .tag("7-alpine")
                .size("32.3MB")
                .build());
                
            images.add(ImageInfo.builder()
                .name("postgres")
                .tag("15")
                .size("379MB")
                .build());
                
            images.add(ImageInfo.builder()
                .name("node")
                .tag("18-alpine")
                .size("172MB")
                .build());
            
            log.debug("더미 이미지 정보 생성 완료: {} 개", images.size());
            return images;
            
        } catch (Exception e) {
            log.warn("이미지 정보 추출 실패: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<NetworkInfo> extractNetworks() {
        try {
            log.debug("네트워크 정보 추출 중 (더미 데이터)...");
            
            List<NetworkInfo> networks = new ArrayList<>();
            
            // 더미 네트워크 데이터 생성
            networks.add(NetworkInfo.builder()
                .name("bridge")
                .driver("bridge")
                .subnet("172.17.0.0/16")
                .build());
                
            networks.add(NetworkInfo.builder()
                .name("app-network")
                .driver("bridge")
                .subnet("172.18.0.0/16")
                .build());
                
            networks.add(NetworkInfo.builder()
                .name("db-network")
                .driver("bridge")
                .subnet("172.19.0.0/16")
                .build());
            
            log.debug("더미 네트워크 정보 생성 완료: {} 개", networks.size());
            return networks;
            
        } catch (Exception e) {
            log.warn("네트워크 정보 추출 실패: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<VolumeInfo> extractVolumes() {
        try {
            log.debug("볼륨 정보 추출 중 (더미 데이터)...");
            
            List<VolumeInfo> volumes = new ArrayList<>();
            
            // 더미 볼륨 데이터 생성
            volumes.add(VolumeInfo.builder()
                .name("redis-data")
                .driver("local")
                .mountpoint("/var/lib/docker/volumes/redis-data/_data")
                .build());
                
            volumes.add(VolumeInfo.builder()
                .name("postgres-data")
                .driver("local")
                .mountpoint("/var/lib/docker/volumes/postgres-data/_data")
                .build());
                
            volumes.add(VolumeInfo.builder()
                .name("app-logs")
                .driver("local")
                .mountpoint("/var/lib/docker/volumes/app-logs/_data")
                .build());
            
            log.debug("더미 볼륨 정보 생성 완료: {} 개", volumes.size());
            return volumes;
            
        } catch (Exception e) {
            log.warn("볼륨 정보 추출 실패: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}