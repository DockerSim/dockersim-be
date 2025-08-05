package com.dockersim.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DockerImageResponse {
    private String name;
    private String namespace;
    private String description;
    private int starCount;
    private long pullCount;
    private LocalDate lastUpdated;
    private LocalDate dateRegistered;
    private String logoUrl;
    private List<String> tags;
}
