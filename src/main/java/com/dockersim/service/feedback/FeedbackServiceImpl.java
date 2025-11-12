package com.dockersim.service.feedback;

import com.dockersim.dto.request.DockerfileFeedbackRequest;
import com.dockersim.dto.response.DockerfileFeedbackResponse;
import org.springframework.stereotype.Service;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Override
    public DockerfileFeedbackResponse getDockerfileFeedback(DockerfileFeedbackRequest request) {
        // TODO: 여기에 실제 AI 모델 호출 또는 복잡한 로직을 구현합니다.
        // 현재는 더미 피드백을 반환합니다.

        long startTime = System.currentTimeMillis();
        String originalContent = request.getDockerfileContent();

        // 피드백 텍스트 생성
        String feedback = "🔍 docker-compose.yml 파일 분석 결과:\n\n" +
                          "✅ 잘된 점:\n" +
                          "• 서비스 정의가 명확합니다\n" +
                          "• 네트워크 구성이 잘 되어 있습니다\n\n" +
                          "⚠️ 개선이 필요한 부분:\n" +
                          "• 이미지 태그를 latest 대신 특정 버전으로 지정하세요\n" +
                          "• healthcheck를 추가하여 서비스 상태를 모니터링하세요\n" +
                          "• 리소스 제한(CPU, Memory)을 설정하세요\n" +
                          "• depends_on으로 서비스 시작 순서를 제어하세요\n" +
                          "• restart 정책을 설정하여 안정성을 높이세요";

        // 최적화된 docker-compose.yml 생성
        String optimized = "version: '3.8'\n\n" +
                          "services:\n" +
                          "  web:\n" +
                          "    image: nginx:1.21-alpine  # 특정 버전 명시\n" +
                          "    container_name: web-server\n" +
                          "    ports:\n" +
                          "      - \"80:80\"\n" +
                          "    volumes:\n" +
                          "      - web-data:/var/www/html\n" +
                          "      - ./nginx.conf:/etc/nginx/nginx.conf:ro  # 설정 파일 마운트\n" +
                          "    networks:\n" +
                          "      - app-network\n" +
                          "    restart: unless-stopped  # 재시작 정책\n" +
                          "    deploy:\n" +
                          "      resources:\n" +
                          "        limits:\n" +
                          "          cpus: '0.5'\n" +
                          "          memory: 512M\n" +
                          "        reservations:\n" +
                          "          cpus: '0.25'\n" +
                          "          memory: 256M\n" +
                          "    healthcheck:\n" +
                          "      test: [\"CMD\", \"curl\", \"-f\", \"http://localhost\"]\n" +
                          "      interval: 30s\n" +
                          "      timeout: 10s\n" +
                          "      retries: 3\n" +
                          "      start_period: 40s\n\n" +
                          "volumes:\n" +
                          "  web-data:\n" +
                          "    driver: local\n\n" +
                          "networks:\n" +
                          "  app-network:\n" +
                          "    driver: bridge";

        long processingTime = System.currentTimeMillis() - startTime;

        return DockerfileFeedbackResponse.builder()
                .original(originalContent)
                .optimized(optimized)
                .feedback(feedback)
                .analysisMethod("Static Analysis + Best Practices")
                .processingTimeMs(processingTime)
                .build();
    }
}
