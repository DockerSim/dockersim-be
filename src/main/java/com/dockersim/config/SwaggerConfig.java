package com.dockersim.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
        @Bean
        public OpenAPI dockerSimOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Docker Simulation API")
                                                .description("Docker 명령어 학습을 위한 시뮬레이션 플랫폼 API")
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("Docker Simulation Team")
                                                                .email("admin@dockersim.com")))
                                .servers(List.of(
                                                new Server().url("http://localhost:8080").description("개발 서버"),
                                                new Server().url("https://api.dockersim.com").description("운영 서버")));
        }
}