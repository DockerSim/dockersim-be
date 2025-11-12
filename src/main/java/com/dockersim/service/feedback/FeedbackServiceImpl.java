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
        String feedback = "AI가 제안하는 개선된 Docker file 파일 내용입니다.\n\n" +
                          "# Docker file 파일 분석 결과:\n" +
                          "# 1. 서비스 정의: 'web' 서비스가 정의되어 있습니다.\n" +
                          "# 2. 이미지: 'nginx:latest' 이미지를 사용하고 있습니다.\n" +
                          "# 3. 포트 매핑: 호스트의 80번 포트와 컨테이너의 80번 포트가 매핑되어 있습니다.\n" +
                          "# 4. 볼륨: 'web-data' 볼륨이 '/var/www/html' 경로에 마운트되어 있습니다.\n\n" +
                          "# 개선 제안:\n" +
                          "# - Nginx 설정 파일을 별도 볼륨으로 마운트하여 관리하는 것을 고려해보세요.\n" +
                          "# - healthcheck를 추가하여 서비스의 상태를 모니터링할 수 있습니다.\n" +
                          "# - 리소스 제한 (CPU, Memory)을 설정하여 안정성을 높일 수 있습니다.\n\n" +
                          "--- 원본 Docker file 파일 ---\n" + request.getDockerfileContent();

        return DockerfileFeedbackResponse.builder()
                .feedback(feedback)
                .build();
    }
}
