package com.dockersim.service.dockerfile;

import org.springframework.stereotype.Component;

/**
 * Dockerfile 분석을 위한 Gemini 프롬프트 생성기
 */
@Component
public class DockerfilePromptBuilder {

    public String buildAnalysisPrompt(String dockerfileContent) {
        return """
                다음 Dockerfile을 분석하여 최적화된 버전을 제공해주세요.
                
                분석 기준:
                1. 이미지 크기 최적화 - alpine, slim 버전 제안
                2. 레이어 캐싱 최적화 - COPY 순서 개선 (package.json 먼저 복사)
                3. 보안 강화 - 비root 사용자, 민감정보 검사
                4. 헬스체크 추가 고려사항 - 프로젝트에 맞게 조정 필요함을 명시
                5. 멀티 스테이지 빌드 고려사항 - 필요시 제안
                
                응답 규칙:
                - 최적화된 Dockerfile만 제공 (설명은 주석으로만)
                - 주석은 간결하고 구체적으로 작성 (이모티콘 사용 금지)
                - 변경 이유와 효과를 주석에서 명확히 설명
                - 헬스체크나 멀티 스테이지는 주석으로 고려사항만 제안
                - 원본 구조와 기능은 유지하되 최적화에 집중
                
                분석할 Dockerfile:
                ```dockerfile
                %s
                ```
                
                최적화된 Dockerfile:
                """.formatted(dockerfileContent);
    }
}