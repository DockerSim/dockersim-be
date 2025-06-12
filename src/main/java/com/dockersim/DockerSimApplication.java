package com.dockersim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DockerSimApplication {
	public static void main(String[] args) {
		SpringApplication.run(DockerSimApplication.class, args);
	}
}