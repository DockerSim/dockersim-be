# Docker 시뮬레이션 학습 플랫폼 API 문서

## 📋 개요

Docker 명령어를 안전하게 학습할 수 있는 시뮬레이션 환경을 제공하는 REST API입니다. 실제 Docker 환경 없이도 컨테이너, 이미지, 네트워크, 볼륨 등의 핵심 개념을 체험할 수 있습니다.

## 🌐 API 접근 정보

- **기본 URL**: `http://localhost:8080`
- **📚 Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **📋 OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **🏥 헬스체크**: `http://localhost:8080/hc`

## ✨ 최신 업데이트

### 🎉 Swagger UI 완전 지원

- **Spring Boot 3.4.4** + **SpringDoc OpenAPI 2.8.9** 호환성 완료
- 실시간 API 테스트 및 문서화 제공
- **Try it out** 기능으로 바로 API 실행 가능
- 모든 응답 스키마 및 예시 완벽 표시

### 🔧 기술 스택

- **Framework**: Spring Boot 3.4.4
- **Documentation**: SpringDoc OpenAPI 2.8.9
- **Database**: JPA/Hibernate
- **Security**: Spring Security (지원 예정)

## 🚀 빠른 시작 가이드

### 1. Swagger UI 접근

1. 애플리케이션 실행: `./gradlew bootRun`
2. 브라우저에서 접속: `http://localhost:8080/swagger-ui.html`
3. **Try it out** 버튼으로 바로 API 테스트 가능

### 2. 기본 사용법

```bash
# 헬스체크 (브라우저에서 직접 접근 가능)
GET http://localhost:8080/hc

# Docker 명령어 실행 (Swagger UI에서 테스트)
POST http://localhost:8080/api/docker/execute
```

## 🎯 주요 기능

### 1. Docker 명령어 실행 API

**엔드포인트**: `POST /api/docker/execute`

Docker 명령어를 파싱하고 시뮬레이션 환경에서 실행합니다.

#### 요청 형식

```json
{
  "command": "string", // 실행할 Docker 명령어
  "simulationId": "string", // 시뮬레이션 세션 ID
  "userId": 0, // 사용자 ID
  "sessionId": "string" // 협업 세션 ID (선택사항)
}
```

#### 응답 형식

```json
{
  "code": "SUCCESS",
  "message": "요청이 성공적으로 처리되었습니다.",
  "data": {
    "command": "string",           // 실행된 명령어
    "output": "string",            // 명령어 실행 결과
    "success": true,               // 성공 여부
    "simulationId": "string",      // 시뮬레이션 ID
    "executedAt": "2024-06-12 15:30:45",
    "containers": [...],           // 현재 컨테이너 목록
    "images": [...],              // 현재 이미지 목록
    "networks": [...],            // 현재 네트워크 목록
    "volumes": [...],             // 현재 볼륨 목록
    "stateChanges": {...},        // 상태 변화 정보
    "hint": "string",             // 학습 힌트
    "help": "string"              // 추가 도움말
  }
}
```

### 2. 헬스체크 API

**엔드포인트**: `GET /hc`

서버 상태 및 환경 정보를 확인합니다.

## 🐳 지원하는 Docker 명령어

### 컨테이너 관리

| 명령어           | 설명                           | 주요 옵션                                             |
| ---------------- | ------------------------------ | ----------------------------------------------------- |
| `docker run`     | 컨테이너 생성 및 실행          | `-d`, `--name`, `-p`, `-v`, `--network`, `-e`, `--rm` |
| `docker create`  | 컨테이너 생성 (실행하지 않음)  | `--name`, `-p`, `-v`, `--network`, `-e`               |
| `docker start`   | 중지된 컨테이너 시작           | 컨테이너 이름/ID                                      |
| `docker stop`    | 실행 중인 컨테이너 중지        | 컨테이너 이름/ID                                      |
| `docker restart` | 컨테이너 재시작                | 컨테이너 이름/ID                                      |
| `docker rm`      | 컨테이너 삭제                  | `-f`, `--force`                                       |
| `docker ps`      | 컨테이너 목록 조회             | `-a`, `--all`                                         |
| `docker exec`    | 실행 중인 컨테이너에 명령 실행 | `-it`, `-d`                                           |
| `docker logs`    | 컨테이너 로그 조회             | `-f`, `--follow`, `--tail`                            |
| `docker inspect` | 컨테이너 상세 정보 조회        | JSON 형태 출력                                        |
| `docker commit`  | 컨테이너를 이미지로 변환       | `-m`, `--message`                                     |

### 이미지 관리

| 명령어           | 설명                      | 주요 옵션                             |
| ---------------- | ------------------------- | ------------------------------------- |
| `docker images`  | 이미지 목록 조회          | `-a`, `--all`                         |
| `docker pull`    | 이미지 다운로드           | 이미지명:태그                         |
| `docker push`    | 이미지 업로드             | 네임스페이스/이미지명:태그            |
| `docker build`   | Dockerfile로 이미지 빌드  | `-t`, `--tag`, `--no-cache`, `--pull` |
| `docker tag`     | 이미지 태그 추가          | 소스:태그 타겟:태그                   |
| `docker rmi`     | 이미지 삭제               | `-f`, `--force`                       |
| `docker inspect` | 이미지 상세 정보 조회     | JSON 형태 출력                        |
| `docker history` | 이미지 레이어 히스토리    | 이미지명:태그                         |
| `docker prune`   | 사용하지 않는 이미지 정리 | `-a`, `--all`, `-f`, `--force`        |

### 네트워크 관리

| 명령어                      | 설명                         | 주요 옵션                           |
| --------------------------- | ---------------------------- | ----------------------------------- |
| `docker network ls`         | 네트워크 목록 조회           | 기본 네트워크 포함                  |
| `docker network create`     | 네트워크 생성                | `--driver`, `--subnet`, `--gateway` |
| `docker network rm`         | 네트워크 삭제                | 네트워크명                          |
| `docker network inspect`    | 네트워크 상세 정보 조회      | JSON 형태, IPAM 설정                |
| `docker network connect`    | 컨테이너를 네트워크에 연결   | IP 자동 할당                        |
| `docker network disconnect` | 컨테이너를 네트워크에서 분리 | 네트워크명 컨테이너명               |

### 볼륨 관리

| 명령어                  | 설명                    | 주요 옵션              |
| ----------------------- | ----------------------- | ---------------------- |
| `docker volume ls`      | 볼륨 목록 조회          | 드라이버 정보 포함     |
| `docker volume create`  | 볼륨 생성               | `--driver`, `--name`   |
| `docker volume rm`      | 볼륨 삭제               | 사용 중 검증           |
| `docker volume inspect` | 볼륨 상세 정보 조회     | JSON 형태, 마운트 정보 |
| `docker volume prune`   | 사용하지 않는 볼륨 정리 | `-f`, `--force`        |

## 📝 명령어 사용 예시

### 기본 컨테이너 작업

```bash
# nginx 컨테이너 실행
docker run -d --name my-nginx -p 8080:80 nginx:latest

# 실행 중인 컨테이너 확인
docker ps

# 컨테이너 로그 확인
docker logs my-nginx

# 컨테이너 중지 및 삭제
docker stop my-nginx
docker rm my-nginx
```

### 네트워크 구성

```bash
# 사용자 정의 네트워크 생성
docker network create --driver bridge --subnet 172.20.0.0/16 my-network

# 네트워크에 컨테이너 연결하여 실행
docker run -d --name web --network my-network nginx

# 기존 컨테이너를 네트워크에 연결
docker network connect my-network my-container

# 네트워크 상세 정보 확인
docker network inspect my-network
```

### 볼륨 관리

```bash
# 데이터 볼륨 생성
docker volume create my-data

# 볼륨을 마운트하여 컨테이너 실행
docker run -d --name db -v my-data:/var/lib/mysql mysql:8.0

# 볼륨 상세 정보 확인
docker volume inspect my-data

# 사용하지 않는 볼륨 정리
docker volume prune -f
```

### 이미지 빌드 및 관리

```bash
# 이미지 빌드
docker build -t my-app:1.0 .

# 이미지 태그 추가
docker tag my-app:1.0 my-registry/my-app:latest

# 컨테이너를 이미지로 저장
docker commit my-container my-app:v2.0

# 이미지 히스토리 확인
docker history my-app:1.0
```

## 🎓 교육적 특징

### 학습 힌트 시스템

- 각 명령어 실행 후 상황에 맞는 학습 힌트 제공
- 다음 단계 가이드 및 관련 명령어 추천
- 오류 발생 시 명확한 설명과 해결 방법 제시

### 실제 Docker와 유사한 경험

- 실제 Docker CLI와 동일한 명령어 구조
- 유사한 출력 형태 및 에러 메시지
- JSON 형태의 상세 메타데이터 제공

### 안전한 실습 환경

- 실제 시스템에 영향 없음
- 무제한 실험 가능
- 상태 초기화 및 스냅샷 기능

## 📖 API 사용 팁

### Swagger UI 활용법

1. **Interactive Testing**: 각 API 엔드포인트에서 "Try it out" 버튼 클릭
2. **Request Body 작성**: JSON 형태로 요청 데이터 입력
3. **즉시 실행**: "Execute" 버튼으로 실제 API 호출
4. **응답 확인**: 실시간으로 응답 데이터 및 상태 코드 확인

### 개발자 도구

- **OpenAPI JSON**: `/v3/api-docs`에서 API 스펙 다운로드 가능
- **헬스체크**: `/hc`로 서버 상태 모니터링
- **디버깅**: 브라우저 개발자 도구로 네트워크 요청 분석

### 호환성 정보

- **Spring Boot**: 3.4.4+
- **SpringDoc OpenAPI**: 2.8.9+
- **Java**: 17+

---

> 💡 **TIP**: Swagger UI에서 직접 API를 테스트해보세요! 별도의 Postman이나 curl 명령어 없이도 모든 기능을 확인할 수 있습니다.

## 🔧 고급 기능

### 시뮬레이션 상태 관리

- 각 세션별 독립적인 Docker 환경
- 실시간 상태 변화 추적
- 명령어 히스토리 관리

### 네트워크 시뮬레이션

- 기본 네트워크 자동 생성 (bridge, host, none)
- 동적 서브넷 할당 (172.x.x.0/24 형태)
- 컨테이너 IP 주소 자동 관리
- 네트워크 연결 상태 JSON 관리

### 볼륨 시뮬레이션

- 로컬 드라이버 지원
- 컨테이너-볼륨 사용 관계 추적
- 마운트 경로 시뮬레이션
- 볼륨 메타데이터 관리

## 🚀 시작하기

### 1. 애플리케이션 실행

```bash
./gradlew.bat bootRun
```

### 2. Swagger UI 접속

브라우저에서 `http://localhost:8080/swagger-ui/index.html` 접속

### 3. API 테스트

Swagger UI에서 직접 API를 테스트하거나 다음과 같이 curl을 사용:

```bash
curl -X POST "http://localhost:8080/api/docker/execute" \
     -H "Content-Type: application/json" \
     -d '{
       "command": "docker run hello-world",
       "simulationId": "test-001",
       "userId": 1
     }'
```

## 📋 응답 코드

- `200 OK`: 요청 성공 (명령어 실행 성공/실패 모두 포함)
- `400 Bad Request`: 잘못된 요청 형식
- `500 Internal Server Error`: 서버 내부 오류

## 🛠️ 기술 스택

- **Spring Boot 3.4.4**
- **Java 17**
- **MySQL** (데이터베이스)
- **JPA/Hibernate** (ORM)
- **SpringDoc OpenAPI** (API 문서화)
- **Lombok** (코드 간소화)

## 📞 지원 및 문의

- **GitHub**: https://github.com/dockersim
- **Email**: support@dockersim.com

---

_이 문서는 Docker 시뮬레이션 학습 플랫폼 v1.0.0 기준으로 작성되었습니다._
