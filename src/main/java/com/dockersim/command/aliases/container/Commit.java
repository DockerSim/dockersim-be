package com.dockersim.command.aliases.container;

import java.util.concurrent.Callable;

import com.dockersim.command.DockerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.service.container.DockerContainerService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command
@RequiredArgsConstructor
public class Commit implements Callable<CommandResult> {

	private final DockerContainerService service;

	@CommandLine.ParentCommand
	private DockerCommand parent;

	@CommandLine.Parameters(index = "0", description = "Docker Image를 만들 기반이 되는 Docker Container 이름 또는 ID")
	private String nameOrHexId;

	@CommandLine.Parameters(index = "1", description = "새로 생성되는 Docker Image 이름")
	private String newImageName;

	@Override
	public CommandResult call() throws Exception {
		return null;
	}
}
