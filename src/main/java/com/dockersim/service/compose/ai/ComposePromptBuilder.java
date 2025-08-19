package com.dockersim.service.compose.ai;

import com.dockersim.dto.request.ComposeGenerationRequest.InfrastructureData;
import com.dockersim.dto.request.ComposeGenerationRequest.ContainerInfo;
import com.dockersim.dto.request.ComposeGenerationRequest.NetworkInfo;
import com.dockersim.dto.request.ComposeGenerationRequest.VolumeInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Docker Compose 생성을 위한 프롬프트 빌더
 */
@Slf4j
@Component
public class ComposePromptBuilder {

    public String build(InfrastructureData infraData) {
        log.debug("프롬프트 생성 시작: containers={}, networks={}, volumes={}", 
            infraData.getContainers() != null ? infraData.getContainers().size() : 0,
            infraData.getNetworks() != null ? infraData.getNetworks().size() : 0,
            infraData.getVolumes() != null ? infraData.getVolumes().size() : 0);

        StringBuilder prompt = new StringBuilder();
        
        prompt.append("다음 Docker 인프라 정보를 바탕으로 최적화된 docker-compose.yml 파일을 생성해주세요.\n\n");

        // 컨테이너 정보 추가
        if (infraData.getContainers() != null && !infraData.getContainers().isEmpty()) {
            prompt.append("## 컨테이너 정보:\n");
            prompt.append(buildContainerInfo(infraData.getContainers()));
            prompt.append("\n");
        }

        // 네트워크 정보 추가
        if (infraData.getNetworks() != null && !infraData.getNetworks().isEmpty()) {
            prompt.append("## 네트워크 정보:\n");
            prompt.append(buildNetworkInfo(infraData.getNetworks()));
            prompt.append("\n");
        }

        // 볼륨 정보 추가
        if (infraData.getVolumes() != null && !infraData.getVolumes().isEmpty()) {
            prompt.append("## 볼륨 정보:\n");
            prompt.append(buildVolumeInfo(infraData.getVolumes()));
            prompt.append("\n");
        }

        // 요구사항 명시
        prompt.append("""
            ## 요구사항:
            1. docker-compose.yml 버전 3.8 사용
            2. 프로덕션 환경에 적합한 구성
            3. 보안 베스트 프랙티스 적용
            4. 적절한 리소스 제한 설정 (memory, cpus)
            5. 헬스체크 포함 (가능한 경우)
            6. 재시작 정책 설정 (restart: unless-stopped)
            7. 로그 드라이버 설정 (json-file, 크기 제한)
            8. 네트워크 보안을 위한 격리 적용
            
            ## 출력 형식:
            - docker-compose.yml 파일 내용만 반환해주세요.
            - 추가 설명이나 주석은 포함하지 마세요.
            - YAML 형식을 정확히 지켜주세요.
            """);

        String result = prompt.toString();
        log.debug("프롬프트 생성 완료: length={}", result.length());
        
        return result;
    }

    private String buildContainerInfo(List<ContainerInfo> containers) {
        return containers.stream()
            .map(this::formatContainerInfo)
            .collect(Collectors.joining("\n"));
    }

    private String formatContainerInfo(ContainerInfo container) {
        StringBuilder info = new StringBuilder();
        info.append("- 컨테이너 이름: ").append(container.getName()).append("\n");
        info.append("  이미지: ").append(container.getImage()).append("\n");
        
        if (container.getPorts() != null && !container.getPorts().isEmpty()) {
            info.append("  포트 매핑: ").append(String.join(", ", container.getPorts())).append("\n");
        }
        
        if (container.getVolumes() != null && !container.getVolumes().isEmpty()) {
            info.append("  볼륨 매핑: ").append(String.join(", ", container.getVolumes())).append("\n");
        }
        
        if (container.getEnvironment() != null && !container.getEnvironment().isEmpty()) {
            info.append("  환경 변수: ").append(String.join(", ", container.getEnvironment())).append("\n");
        }
        
        if (container.getNetworkMode() != null) {
            info.append("  네트워크 모드: ").append(container.getNetworkMode()).append("\n");
        }
        
        if (container.getStatus() != null) {
            info.append("  상태: ").append(container.getStatus()).append("\n");
        }
        
        return info.toString();
    }

    private String buildNetworkInfo(List<NetworkInfo> networks) {
        return networks.stream()
            .map(this::formatNetworkInfo)
            .collect(Collectors.joining("\n"));
    }

    private String formatNetworkInfo(NetworkInfo network) {
        StringBuilder info = new StringBuilder();
        info.append("- 네트워크 이름: ").append(network.getName()).append("\n");
        
        if (network.getDriver() != null) {
            info.append("  드라이버: ").append(network.getDriver()).append("\n");
        }
        
        if (network.getSubnet() != null) {
            info.append("  서브넷: ").append(network.getSubnet()).append("\n");
        }
        
        return info.toString();
    }

    private String buildVolumeInfo(List<VolumeInfo> volumes) {
        return volumes.stream()
            .map(this::formatVolumeInfo)
            .collect(Collectors.joining("\n"));
    }

    private String formatVolumeInfo(VolumeInfo volume) {
        StringBuilder info = new StringBuilder();
        info.append("- 볼륨 이름: ").append(volume.getName()).append("\n");
        
        if (volume.getDriver() != null) {
            info.append("  드라이버: ").append(volume.getDriver()).append("\n");
        }
        
        if (volume.getMountpoint() != null) {
            info.append("  마운트 포인트: ").append(volume.getMountpoint()).append("\n");
        }
        
        return info.toString();
    }
}