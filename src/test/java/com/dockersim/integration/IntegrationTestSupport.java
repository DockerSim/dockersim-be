package com.dockersim.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("dev") // "dev" 프로파일을 활성화하여 모든 통합 테스트의 컨텍스트 로딩 오류를 해결합니다.
@SpringBootTest
@Transactional
public abstract class IntegrationTestSupport {

}
