# Docker 시뮬레이션 학습 플랫폼 백엔드

실제 Docker 환경 없이도 Docker 명령어를 안전하게 학습할 수 있는 시뮬레이션 플랫폼의 백엔드 API입니다.

## 🚀 주요 기능

### ✅ 완전 구현된 Docker 시뮬레이션

- **컨테이너 관리**: run, create, start, stop, restart, rm, ps, exec, logs, inspect, commit
- **이미지 관리**: pull, push, build, tag, rmi, images, inspect, history, prune
- **네트워크 관리**: create, ls, rm, inspect, connect, disconnect
- **볼륨 관리**: create, ls, rm, inspect, prune
- **고급 기능**: 포트 매핑, 볼륨 마운트, 네트워크 연결, 환경 변수

### 🎓 교육 중심 설계

- 실제 Docker CLI와 동일한 명령어 구조
- 상황별 학습 힌트 및 가이드 제공
- 실제 Docker와 유사한 JSON 메타데이터 출력
- 안전한 실습 환경 (실제 시스템에 영향 없음)

### 📚 완벽한 API 문서화

- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **상세 API 문서**: [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)
- 모든 명령어별 옵션 및 사용 예시 포함

## 🛠️ 기술 스택

- **Spring Boot 3.4.4** (Java 17)
- **JPA/Hibernate** (데이터 영속성)
- **MySQL** (데이터베이스)
- **SpringDoc OpenAPI** (API 문서화)
- **Lombok** (코드 간소화)

## 🏃‍♂️ 빠른 시작

### 1. 애플리케이션 실행

```bash
./gradlew.bat bootRun
```

### 2. API 테스트

```bash
curl -X POST "http://localhost:8080/api/docker/execute" \
     -H "Content-Type: application/json" \
     -d '{
       "command": "docker run -d --name my-nginx -p 8080:80 nginx:latest",
       "simulationId": "test-001",
       "userId": 1
     }'
```

### 3. Swagger UI 접속

브라우저에서 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) 접속

## 📋 API 엔드포인트

| 메서드 | 경로                     | 설명               |
| ------ | ------------------------ | ------------------ |
| `POST` | `/api/docker/execute`    | Docker 명령어 실행 |
| `GET`  | `/hc`                    | 헬스체크           |
| `GET`  | `/swagger-ui/index.html` | API 문서           |

## 🐳 지원하는 Docker 명령어

<details>
<summary><b>컨테이너 관리 (11개 명령어)</b></summary>

- `docker run` - 컨테이너 생성 및 실행
- `docker create` - 컨테이너 생성 (실행하지 않음)
- `docker start/stop/restart` - 컨테이너 생명주기 관리
- `docker rm` - 컨테이너 삭제
- `docker ps` - 컨테이너 목록 조회
- `docker exec` - 컨테이너 내 명령어 실행
- `docker logs` - 컨테이너 로그 조회
- `docker inspect` - 상세 정보 조회
- `docker commit` - 컨테이너를 이미지로 변환
</details>

<details>
<summary><b>이미지 관리 (9개 명령어)</b></summary>

- `docker images` - 이미지 목록 조회
- `docker pull/push` - 이미지 다운로드/업로드
- `docker build` - Dockerfile로 이미지 빌드
- `docker tag` - 이미지 태그 관리
- `docker rmi` - 이미지 삭제
- `docker inspect` - 이미지 상세 정보
- `docker history` - 이미지 레이어 히스토리
- `docker prune` - 미사용 이미지 정리
</details>

<details>
<summary><b>네트워크 관리 (6개 명령어)</b></summary>

- `docker network ls` - 네트워크 목록 조회
- `docker network create` - 네트워크 생성
- `docker network rm` - 네트워크 삭제
- `docker network inspect` - 네트워크 상세 정보
- `docker network connect/disconnect` - 컨테이너 네트워크 연결/분리
</details>

<details>
<summary><b>볼륨 관리 (5개 명령어)</b></summary>

- `docker volume ls` - 볼륨 목록 조회
- `docker volume create` - 볼륨 생성
- `docker volume rm` - 볼륨 삭제
- `docker volume inspect` - 볼륨 상세 정보
- `docker volume prune` - 미사용 볼륨 정리
</details>

## 📁 프로젝트 구조

```
src/main/java/com/dockersim/
├── controller/          # REST API 컨트롤러
├── service/            # 비즈니스 로직
│   ├── ContainerSimulationService.java
│   ├── ImageSimulationService.java
│   ├── NetworkSimulationService.java
│   ├── VolumeSimulationService.java
│   └── DockerSimulationService.java
├── repository/         # 데이터 접근 계층
├── entity/            # JPA 엔티티
├── dto/               # 데이터 전송 객체
├── parser/            # Docker 명령어 파서
└── config/            # 설정 클래스
```

## 🎯 교육적 특징

### 실제 Docker와 동일한 경험

```bash
# 실제 Docker 명령어와 동일
docker run -d --name web -p 8080:80 --network my-net -v data:/app nginx:latest
docker ps -a
docker logs web
docker inspect web
```

### 학습 힌트 시스템

- 명령어 실행 후 상황별 가이드 제공
- 다음 단계 추천 및 관련 명령어 안내
- 오류 발생 시 명확한 해결 방법 제시

### 고급 시뮬레이션 기능

- 네트워크 IP 자동 할당 (172.x.x.0/24)
- 볼륨 마운트 관계 추적
- 컨테이너 간 네트워크 연결 시뮬레이션
- JSON 메타데이터로 실제 Docker inspect와 유사한 출력

## 📚 문서

- **[완전한 API 문서](./API_DOCUMENTATION.md)** - 모든 명령어와 옵션 상세 설명
- **[Swagger UI](http://localhost:8080/swagger-ui/index.html)** - 대화형 API 테스트
- **[OpenAPI 스펙](http://localhost:8080/api-docs)** - API 명세서

## 🔧 개발 환경

### 필수 요구사항

- Java 17+
- MySQL 8.0+
- Gradle 7.0+

### 로컬 개발 설정

1. MySQL 데이터베이스 생성
2. `application-local.yml` 설정
3. `./gradlew.bat bootRun` 실행

## 📈 향후 계획

- [ ] WebSocket 기반 실시간 협업 기능
- [ ] Docker Compose 시뮬레이션
- [ ] Kubernetes 기본 개념 시뮬레이션
- [ ] 튜토리얼 시스템 강화

---

**개발팀**: Docker Simulation Team  
**라이센스**: Apache 2.0  
**버전**: 1.0.0
