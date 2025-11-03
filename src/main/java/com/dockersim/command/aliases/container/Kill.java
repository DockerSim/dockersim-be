package com.dockersim.command.aliases.container;

import java.util.concurrent.Callable;

import com.dockersim.command.DockerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.service.container.DockerContainerService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "kill")
@RequiredArgsConstructor
public class Kill implements Callable<CommandResult> {

	private final DockerContainerService service;

	@CommandLine.ParentCommand
	private DockerCommand parent;

	@CommandLine.Parameters(index = "0", description = "강제 종료할 Container의 이름 또는 ID")
	private String nameOrHexId;

	@Override
	public CommandResult call() throws Exception {
		return null;
	}
}
