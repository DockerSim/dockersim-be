package com.dockersim.controller;

import com.dockersim.common.response.ApiResponse;
import com.dockersim.common.response.CommonResponseCode;
import com.dockersim.service.DockerCommandParser;
import com.dockersim.service.DockerSimulatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sim")
public class DockerCommandController {
    private final DockerSimulatorService simulatorService;

    @PostMapping("/execute")
    public ApiResponse<String> executeCommand(@RequestBody String command) {
        String feedback = simulatorService.simulate(command);


        return ApiResponse.success(CommonResponseCode.SUCCESS, feedback);
    }
}
