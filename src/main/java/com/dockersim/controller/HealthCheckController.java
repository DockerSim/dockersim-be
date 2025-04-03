package com.dockersim.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hc")
public class HealthCheckController {
    @Value("${server.env}")
    private String env;

    @GetMapping
    public String healthCheck() {
        return env;
    }
}
