package com.dockersim.command.subcommand.container;

import java.util.concurrent.Callable;

import com.dockersim.command.subcommand.ContainerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.service.container.DockerContainerService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "cp")
@RequiredArgsConstructor
public class ContainerCp implements Callable<CommandResult> {

	private final DockerContainerService service;

	@CommandLine.ParentCommand
	private ContainerCommand parent;

	@CommandLine.Parameters(index = "0", description = "호스트와 컨테이너 간 파일 복사 경로")
	private String path;

	@Override
	public CommandResult call() throws Exception {
		return null;
	}
}
