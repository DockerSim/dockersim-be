package com.dockersim.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Docker Hub 공식 이미지 목록을 관리하는 서비스
 * 
 * Docker Hub 공식 이미지는 네임스페이스 없이 사용 가능한 이미지들입니다.
 * 예: nginx, redis, mysql, postgres 등
 * 
 * 비공식 이미지는 반드시 네임스페이스가 필요합니다.
 * 예: myuser/myapp, company/webapp 등
 */
@Service
@Slf4j
public class DockerHubOfficialImageService {

    private Map<String, OfficialImageInfo> officialImages = new HashMap<>();
    private static final String CSV_FILE_PATH = "static/official_docker_images.csv";

    @PostConstruct
    public void loadOfficialImages() {
        log.info("공식 Docker 이미지 데이터 로딩 시작");

        try {
            officialImages = new HashMap<>();
            loadImagesFromCsv();

            log.info("공식 Docker 이미지 데이터 로딩 완료: {} 개 이미지", officialImages.size());
        } catch (Exception e) {
            log.error("공식 Docker 이미지 데이터 로딩 실패", e);
            officialImages = new HashMap<>();
        }
    }

    private void loadImagesFromCsv() throws IOException {
        ClassPathResource resource = new ClassPathResource(CSV_FILE_PATH);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                parseCsvLine(line);
            }
        }
    }

    private void parseCsvLine(String line) {
        if (!StringUtils.hasText(line)) {
            return;
        }

        String[] parts = line.split(",");
        if (parts.length >= 2) {
            String imageName = parts[0].trim();
            String logoUrl = parts[1].trim();

            // 태그들 추출 (2번째 인덱스부터)
            Set<String> tags = new HashSet<>();
            for (int i = 2; i < parts.length; i++) {
                String tag = parts[i].trim();
                if (StringUtils.hasText(tag)) {
                    tags.add(tag);
                }
            }

            // 기본 태그 추가
            if (tags.isEmpty()) {
                tags.add("latest");
            }

            if (StringUtils.hasText(imageName) && StringUtils.hasText(logoUrl)) {
                OfficialImageInfo imageInfo = new OfficialImageInfo(imageName, logoUrl, tags);
                officialImages.put(imageName.toLowerCase(), imageInfo);
            }
        }
    }

    // 공식 이미지 정보를 담는 내부 클래스
    private static class OfficialImageInfo {
        private final String name;
        private final String logoUrl;
        private final Set<String> availableTags;

        public OfficialImageInfo(String name, String logoUrl, Set<String> availableTags) {
            this.name = name;
            this.logoUrl = logoUrl;
            this.availableTags = availableTags;
        }

        public String getName() {
            return name;
        }

        public String getLogoUrl() {
            return logoUrl;
        }

        public Set<String> getAvailableTags() {
            return availableTags;
        }
    }

    /**
     * 이미지 이름이 Docker Hub 공식 이미지인지 확인
     * 
     * @param imageName 이미지 이름 (네임스페이스 제외)
     * @return 공식 이미지 여부
     */
    public boolean isOfficialImage(String imageName) {
        if (!StringUtils.hasText(imageName)) {
            return false;
        }
        return officialImages.containsKey(imageName.toLowerCase());
    }

    /**
     * 공식 이미지의 특정 태그가 존재하는지 확인
     * 
     * @param imageName 이미지 이름
     * @param tag       태그
     * @return 태그 존재 여부
     */
    public boolean isValidOfficialImageTag(String imageName, String tag) {
        if (!isOfficialImage(imageName)) {
            return false;
        }

        OfficialImageInfo imageInfo = officialImages.get(imageName.toLowerCase());
        if (imageInfo == null) {
            return false;
        }

        Set<String> validTags = imageInfo.getAvailableTags();
        return validTags.contains(tag) || "latest".equals(tag);
    }

    /**
     * 이미지 이름에 네임스페이스가 포함되어 있는지 확인
     * 
     * @param imageName 전체 이미지 이름
     * @return 네임스페이스 포함 여부
     */
    public boolean hasNamespace(String imageName) {
        return imageName != null && imageName.contains("/");
    }

    /**
     * 이미지 이름에서 네임스페이스 추출
     * 
     * @param imageName 전체 이미지 이름
     * @return 네임스페이스 (없으면 null)
     */
    public String extractNamespace(String imageName) {
        if (!hasNamespace(imageName)) {
            return null;
        }

        int slashIndex = imageName.lastIndexOf('/');
        return imageName.substring(0, slashIndex);
    }

    /**
     * 이미지 이름에서 실제 이미지명 추출 (네임스페이스 제외)
     * 
     * @param imageName 전체 이미지 이름
     * @return 순수 이미지명
     */
    public String extractImageName(String imageName) {
        if (!hasNamespace(imageName)) {
            return imageName;
        }

        int slashIndex = imageName.lastIndexOf('/');
        return imageName.substring(slashIndex + 1);
    }

    /**
     * 이미지 pull 가능 여부 검증
     * 네임스페이스가 없는 이미지명은 CSV 파일의 공식 이미지 목록에서 먼저 확인
     * 
     * @param imageName 이미지 이름
     * @param tag       태그
     * @return 검증 결과
     */
    public ImageValidationResult validateImagePull(String imageName, String tag) {
        if (imageName == null || imageName.trim().isEmpty()) {
            return ImageValidationResult.error("이미지 이름이 비어있습니다.");
        }

        // 네임스페이스가 있는 경우 (비공식 이미지)
        if (hasNamespace(imageName)) {
            String namespace = extractNamespace(imageName);
            String actualImageName = extractImageName(imageName);

            return ImageValidationResult.success(
                    String.format(
                            "Pulling from %s/%s\n%s: Pulling from %s/%s\nlatest: Pull complete\nDigest: sha256:abc123...\nStatus: Downloaded newer image for %s/%s:%s",
                            namespace, actualImageName, tag, namespace, actualImageName,
                            namespace, actualImageName, tag),
                    false);
        }

        // 네임스페이스가 없는 경우 - CSV 파일의 공식 이미지 목록에서 확인
        OfficialImageInfo imageInfo = officialImages.get(imageName.toLowerCase());
        if (imageInfo == null) {
            return ImageValidationResult.error(
                    String.format("Unable to find image '%s' in Docker Hub official images. " +
                            "If this is a custom image, please include the namespace (e.g., username/%s)",
                            imageName, imageName));
        }

        // 공식 이미지인 경우 태그 유효성 확인
        Set<String> availableTags = imageInfo.getAvailableTags();
        if (!availableTags.contains(tag) && !"latest".equals(tag)) {
            String tagList = String.join(", ", availableTags);
            return ImageValidationResult.error(
                    String.format("Tag '%s' not found for official image '%s'. " +
                            "Available tags: %s", tag, imageName, tagList));
        }

        return ImageValidationResult.success(
                String.format(
                        "Pulling from library/%s\n%s: Pulling from library/%s\nlatest: Pull complete\nDigest: sha256:def456...\nStatus: Downloaded newer image for %s:%s",
                        imageName, tag, imageName, imageName, tag),
                true);
    }

    /**
     * 공식 이미지 목록 반환 (이미지명만)
     */
    public Set<String> getOfficialImages() {
        return officialImages.keySet();
    }

    /**
     * 특정 공식 이미지의 사용 가능한 태그 목록 반환
     */
    public Set<String> getOfficialImageTags(String imageName) {
        OfficialImageInfo imageInfo = officialImages.get(imageName.toLowerCase());
        return imageInfo != null ? new HashSet<>(imageInfo.getAvailableTags()) : Set.of("latest");
    }

    /**
     * 공식 이미지 통계 정보
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalImages", officialImages.size());
        return stats;
    }

    /**
     * 이미지 pull 명령어 추천
     */
    public String generatePullCommand(String imageName) {
        return generatePullCommand(imageName, "latest");
    }

    public String generatePullCommand(String imageName, String tag) {
        if (!StringUtils.hasText(imageName)) {
            return "";
        }

        String fullImageName = StringUtils.hasText(tag) ? imageName + ":" + tag : imageName;
        return "docker pull " + fullImageName;
    }

    /**
     * 이미지 실행 명령어 추천
     */
    public List<String> generateRunCommands(String imageName) {
        if (!StringUtils.hasText(imageName)) {
            return List.of();
        }

        return switch (imageName.toLowerCase()) {
            case "nginx" -> List.of(
                    "docker run -d -p 80:80 nginx",
                    "docker run -d -p 8080:80 --name my-nginx nginx",
                    "docker run -d -p 80:80 -v /path/to/html:/usr/share/nginx/html nginx");
            case "mysql" -> List.of(
                    "docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=password mysql",
                    "docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=mydb mysql",
                    "docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=password -v mysql_data:/var/lib/mysql mysql");
            case "postgres" -> List.of(
                    "docker run -d -p 5432:5432 -e POSTGRES_PASSWORD=password postgres",
                    "docker run -d -p 5432:5432 -e POSTGRES_USER=myuser -e POSTGRES_PASSWORD=password postgres",
                    "docker run -d -p 5432:5432 -e POSTGRES_DB=mydb -e POSTGRES_PASSWORD=password postgres");
            case "redis" -> List.of(
                    "docker run -d -p 6379:6379 redis",
                    "docker run -d -p 6379:6379 --name my-redis redis",
                    "docker run -d -p 6379:6379 redis redis-server --appendonly yes");
            case "mongo" -> List.of(
                    "docker run -d -p 27017:27017 mongo",
                    "docker run -d -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=admin -e MONGO_INITDB_ROOT_PASSWORD=password mongo",
                    "docker run -d -p 27017:27017 -v mongo_data:/data/db mongo");
            default -> List.of(
                    "docker run -it " + imageName,
                    "docker run -d --name my-" + imageName + " " + imageName,
                    "docker run --rm " + imageName);
        };
    }

    /**
     * 이미지 검증 결과 클래스
     */
    public static class ImageValidationResult {
        private final boolean valid;
        private final String message;
        private final boolean isOfficial;

        private ImageValidationResult(boolean valid, String message, boolean isOfficial) {
            this.valid = valid;
            this.message = message;
            this.isOfficial = isOfficial;
        }

        public static ImageValidationResult success(String message, boolean isOfficial) {
            return new ImageValidationResult(true, message, isOfficial);
        }

        public static ImageValidationResult error(String message) {
            return new ImageValidationResult(false, message, false);
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }

        public boolean isOfficial() {
            return isOfficial;
        }
    }
}