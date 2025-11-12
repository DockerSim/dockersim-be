package com.dockersim.service.dockerfile;

import org.springframework.stereotype.Component;

/**
 * Dockerfile 분석을 위한 Gemini 프롬프트 생성기
 */
@Component
public class DockerfilePromptBuilder {

    public String buildAnalysisPrompt(String dockerfileContent) {
        return """
                다음 내용을 분석하여 최적화된 Dockerfile을 제공해주세요.

                입력 유형:
                - Dockerfile인 경우: 분석 후 최적화된 버전 제공
                - 수도 코드/요구사항인 경우: Dockerfile로 변환

                최적화 기준:
                1. 이미지 크기 최적화 - alpine, slim 버전 사용
                2. 레이어 캐싱 최적화 - 의존성 파일 먼저 복사
                3. 보안 강화 - 비root 사용자 사용
                4. 멀티 스테이지 빌드 - 필요시 적용
                5. 헬스체크 - 프로젝트에 맞게 추가

                응답 규칙 (매우 중요):
                - Dockerfile 코드가 주, 주석은 보조
                - 주석은 한 줄로 핵심만 간결하게 (이모티콘 금지)
                - 장황한 설명 금지 (예: "# 이미지 크기 최적화", "# 레이어 캐싱")
                - 불필요한 주석 제거 (당연한 내용 생략)
                - 실행 가능한 Dockerfile 코드 제공

                입력 내용:
                ```
                %s
                ```

                최적화된 Dockerfile (주석은 핵심만 한 줄로):
                """.formatted(dockerfileContent);
    }
}