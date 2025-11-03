package com.dockersim.command.aliases.container;

import java.util.concurrent.Callable;

import com.dockersim.command.DockerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.service.container.DockerContainerService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "cp")
@RequiredArgsConstructor
public class Cp implements Callable<CommandResult> {

	private final DockerContainerService service;

	@CommandLine.ParentCommand
	private DockerCommand parent;

	@CommandLine.Parameters(index = "0", description = "호스트와 컨테이너 간 파일 복사 경로")
	private String path;

	@Override
	public CommandResult call() throws Exception {
		return null;
	}
}
