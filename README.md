# 🐳 Docker Simulator - 협업 기반 학습 플랫폼

> **"실제 Docker 환경 없이도 안전하게 Docker를 배울 수 있는 교육용 시뮬레이션 플랫폼"**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-red.svg)](https://openjdk.java.net/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Test Coverage](https://img.shields.io/badge/Test%20Coverage-100%25-brightgreen.svg)](#)

---

## 🎯 **프로젝트 개요**

Docker 명령어를 처음 배우는 개발자들을 위한 **협업 기반 학습 플랫폼**입니다. 실제 Docker 환경의 위험성 없이 명령어를 실습하고, 팀원들과 함께 학습할 수 있는 안전한 시뮬레이션 환경을 제공합니다.

### 💡 **해결하려는 문제**

- Docker 명령어 실수로 인한 시스템 장애 위험
- 혼자 학습하기 어려운 Docker 개념들
- 팀 단위 Docker 교육의 어려움
- 실습 환경 구축의 복잡성

### 🌟 **핵심 가치**

- **안전한 학습**: 실제 시스템에 영향 없는 시뮬레이션 환경
- **협업 중심**: 팀원과 함께 배우는 공유 학습 공간
- **점진적 학습**: 단계별 명령어 학습 가이드
- **실무 연계**: 실제 Docker CLI와 동일한 명령어 구조

---

## 🏗️ **아키텍처 & 기술적 도전**

### **시스템 아키텍처**

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend API   │    │   Database      │
│   (Nextjs)      │◄──►│  (Spring Boot)  │◄──►│    (MySQL)      │
│                 │    │                 │    │                 │
│ • CLI Simulator │    │ • Docker Parser │    │ • User Data     │
│ • Visual Grid   │    │ • State Manager │    │ • Simulations   │
│ • Collaboration │    │ • Collaboration │    │ • Containers    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### **핵심 기술적 도전과 해결책**

#### 🔧 **1. Docker 명령어 파싱 엔진**

**도전**: 복잡한 Docker CLI 명령어를 정확히 파싱하고 시뮬레이션

```java
// 복잡한 명령어 예시
docker run -d --name web -p 8080:80 -v data:/app --network custom nginx:latest
```

**해결책**:

- 토큰 기반 파서 설계
- 옵션별 전용 핸들러 구현
- 교육 목적에 맞는 선별적 지원

#### 📊 **2. 실시간 상태 동기화**

**도전**: 명령어 실행 결과를 프론트엔드에 효율적으로 전달

```json
{
  "stateChanges": {
    "containers": {"added": [...], "modified": [...], "removed": [...]},
    "images": {"added": [...], "removed": [...]},
    "summary": {"totalContainers": 3, "runningContainers": 2}
  }
}
```

**해결책**:

- 변경사항 기반 업데이트 (전체 재조회 없이)
- 백엔드 계산 카운터로 정확성 보장
- 즉시 UI 반영으로 사용자 경험 개선

#### 🤝 **3. 협업 권한 관리 시스템**

**도전**: 복잡한 시뮬레이션 공유 및 협업자 권한 관리

```java
public enum ShareState { PRIVATE, READ, WRITE }
public enum Permission { READ, WRITE }

// 협업자가 자신을 제거할 수 있는 정교한 권한 로직
public void removeCollaborator(Long simulationId, Long collaboratorUserId, Long requesterId) {
    boolean isOwner = simulation.isOwner(requester);
    boolean isSelfRemoval = requester.getId().equals(collaboratorUserId);

    if (!isOwner && !isSelfRemoval) {
        throw SimulationException.onlyOwnerCanManage(requesterId, simulationId);
    }
}
```

**해결책**:

- 역할 기반 접근 제어 (RBAC) 구현
- 소유자/협업자 권한 분리
- 이메일 기반 협업자 초대 시스템

---


## 🛠️ **기술 스택**

### **Backend**

- **Spring Boot 3.4.4** - 최신 프레임워크 활용
- **Spring Data JPA** - 객체-관계 매핑
- **MySQL 8.0** - 안정적인 데이터 저장
- **SpringDoc OpenAPI** - API 문서 자동화
- **JUnit 5** - 현대적 테스트 프레임워크

### **Frontend**

- **Vue.js 3** - 반응형 사용자 인터페이스
- **JavaScript ES6+** - 모던 웹 개발
- **CSS Grid/Flexbox** - 유연한 레이아웃

### **개발 도구**

- **Gradle** - 빌드 자동화
- **Git** - 버전 관리
- **IntelliJ IDEA** - 통합 개발 환경

---

## 🏃‍♂️ **빠른 실행**

```bash
# 1. 저장소 클론
git clone https://github.com/your-username/dockersim-be.git
cd dockersim-be

# 2. 애플리케이션 실행
./gradlew bootRun

# 3. Swagger UI 접속
open http://localhost:8080/swagger-ui.html
```

### **API 테스트 예시**

```bash
# 사용자 생성
curl -X POST "http://localhost:8080/api/users" \
  -H "Content-Type: application/json" \
  -d '{"name": "김개발", "email": "dev@example.com"}'

# 시뮬레이션 생성
curl -X POST "http://localhost:8080/api/simulations" \
  -H "Content-Type: application/json" \
  -d '{"title": "Docker 입문", "shareState": "PRIVATE", "ownerId": 1}'
```

---

<div align="center">

**"안전하게 배우고, 함께 성장하는 Docker 학습의 새로운 패러다임"**

⭐ **이 프로젝트가 도움이 되셨다면 Star를 눌러주세요!**

</div>
