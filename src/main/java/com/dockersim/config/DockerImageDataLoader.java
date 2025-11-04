package com.dockersim.config;

import com.dockersim.service.image.DockerOfficeImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "spring.jpa.hibernate.ddl-auto",
        havingValue = "create",
        matchIfMissing = false
)
@RequiredArgsConstructor
public class DockerImageDataLoader implements CommandLineRunner {

    private final DockerOfficeImageService service;

    @Override
    public void run(String... args) throws Exception {
        service.loadAllFromJson();
    }
}
