package com.dockersim.command.subcommand.container;

import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import com.dockersim.command.subcommand.ContainerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.service.container.DockerContainerService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "kill")
@Component
@RequiredArgsConstructor
public class ContainerKill implements Callable<CommandResult> {

	private final DockerContainerService service;

	@CommandLine.ParentCommand
	private final ContainerCommand parent;

	@CommandLine.Parameters(index = "0", description = "강제 종료할 Container의 이름 또는 ID")
	private String nameOrHexId;

	@Override
	public CommandResult call() throws Exception {
		return null;
	}
}
