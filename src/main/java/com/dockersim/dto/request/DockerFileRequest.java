package com.dockersim.dto.request;

import lombok.Getter;

@Getter
public class DockerFileRequest {
	private String name;
	private String path;
	private String content;
}
