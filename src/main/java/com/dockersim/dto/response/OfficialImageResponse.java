package com.dockersim.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class OfficialImageResponse {

    private final String imageName;
    private final String logoUrl;
    private final boolean isOfficial;
    private final String description;

    public static OfficialImageResponse of(String imageName, String logoUrl) {
        return OfficialImageResponse.builder()
                .imageName(imageName)
                .logoUrl(logoUrl)
                .isOfficial(true)
                .description(generateDescription(imageName))
                .build();
    }

    private static String generateDescription(String imageName) {
        return switch (imageName.toLowerCase()) {
            case "ubuntu" -> "Ubuntu Linux 운영 체제의 공식 Docker 이미지";
            case "centos" -> "CentOS Linux 운영 체제의 공식 Docker 이미지";
            case "nginx" -> "고성능 웹 서버 및 리버스 프록시 서버";
            case "mysql" -> "세계에서 가장 인기 있는 오픈소스 관계형 데이터베이스";
            case "postgres" -> "강력한 오픈소스 관계형 데이터베이스 시스템";
            case "redis" -> "고성능 인메모리 데이터 구조 저장소";
            case "node" -> "JavaScript 런타임 환경";
            case "python" -> "Python 프로그래밍 언어 런타임";
            case "java", "openjdk" -> "Java 프로그래밍 언어 런타임";
            case "golang" -> "Go 프로그래밍 언어 런타임";
            case "php" -> "PHP 프로그래밍 언어 런타임";
            case "ruby" -> "Ruby 프로그래밍 언어 런타임";
            case "alpine" -> "보안 지향적이고 가벼운 Alpine Linux";
            case "busybox" -> "작고 빠른 Unix 도구들의 모음";
            case "hello-world" -> "Docker 테스트용 간단한 Hello World 이미지";
            case "wordpress" -> "인기 있는 컨텐츠 관리 시스템";
            case "mongo" -> "NoSQL 문서 지향 데이터베이스";
            case "elasticsearch" -> "분산 검색 및 분석 엔진";
            case "jenkins" -> "지속적 통합 및 배포 도구";
            case "tomcat" -> "Java Servlet 컨테이너 및 웹 서버";
            case "httpd" -> "Apache HTTP 서버";
            case "rabbitmq" -> "신뢰할 수 있는 메시지 브로커";
            case "docker" -> "Docker-in-Docker 실행을 위한 이미지";
            default -> imageName + " 공식 Docker 이미지";
        };
    }
}