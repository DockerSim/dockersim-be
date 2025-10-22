package com.dockersim.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import picocli.CommandLine;
import picocli.spring.boot.autoconfigure.PicocliSpringFactory;

@Configuration
public class PicocliConfig {

    @Bean
    public CommandLine.IFactory picocliSpringFactory(ApplicationContext ctx) {
        return new PicocliSpringFactory(ctx);
    }
}
