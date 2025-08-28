package com.dockersim.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String HEADER_NAME = "X-User-Id";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("DockerSim API Document")
                .description("Docker 명령어 학습을 위한 시뮬레이션 서비스의 API 명세서입니다.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Docker Simulation Team")
                    .email("yrkim6883@gmail.com"))
            )
            .servers(List.of(
                new Server().url("http://localhost:8080").description("개발 서버")
            ))
            .components(new Components()
                .addSecuritySchemes(HEADER_NAME,
                    new SecurityScheme()
                        .name(HEADER_NAME)
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .description("개발용 사용자 Public ID 주입 헤더"))
            )
            .addSecurityItem(new SecurityRequirement().addList(HEADER_NAME));
    }
}
