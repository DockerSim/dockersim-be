package com.dockersim.service.dockerfile;

import org.springframework.stereotype.Component;

/**
 * Dockerfile 분석을 위한 Gemini 프롬프트 생성기
 */
@Component
public class DockerfilePromptBuilder {

    public String buildAnalysisPrompt(String dockerfileContent) {
        return """
                다음 내용을 분석해 최적화된 Dockerfile을 출력하세요.

                입력 유형:
                - Dockerfile: 문제점 분석 후 최적화된 Dockerfile 제시
                - 수도코드/요구사항: 프레임워크 유형 감지(Express/Nest/Next/Flask/Spring 등) 후 적절한 Dockerfile 생성

                최적화 규칙(엄수):
                1) 베이스 이미지: 가능한 alpine/slim 사용. 필요 시에만 build-base 설치
                2) 레이어 캐싱: 의존성 파일(package.json, requirements.txt, pom.xml 등) 먼저 COPY
                3) 멀티스테이지: 빌드/런타임 분리, 최종 이미지에는 필수 파일만
                4) 보안: 비root 사용자, COPY --chown 사용
                5) 실행 최적화: 프레임워크별 적절한 실행 커맨드 (Next.js: npm start, Nest: node dist/main.js 등)
                6) 헬스체크: 프로젝트에 맞게 추가

                출력 형식(매우 중요):
                섹션 A - 최종 Dockerfile:
                ```dockerfile
                (코드만, 필수 주석만 한 줄로)
                ```

                섹션 B - .dockerignore:
                ```
                node_modules
                .git
                *.log
                (프로젝트별 추가 항목)
                ```

                섹션 C - 개선 요약:
                - 변경사항 1
                - 변경사항 2
                - 변경사항 3

                입력 내용:
                ```
                %s
                ```

                주의사항:
                - 전체 폴더 COPY 금지 (.git, tests 제외)
                - 멀티스테이지 시 최종 단계에 필요한 것만 COPY
                - 권한 설정: COPY --chown=node:node 후 USER node
                - 불필요한 주석/이모지 금지, 실행 가능한 코드 우선
                """.formatted(dockerfileContent);
    }
}