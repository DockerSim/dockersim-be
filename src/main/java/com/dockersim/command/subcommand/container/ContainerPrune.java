package com.dockersim.command.subcommand.container;

import java.util.concurrent.Callable;

import com.dockersim.command.subcommand.ContainerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.service.container.DockerContainerService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "prune")
@RequiredArgsConstructor
public class ContainerPrune implements Callable<CommandResult> {

	private final DockerContainerService service;

	@CommandLine.ParentCommand
	private ContainerCommand parent;

	@Override
	public CommandResult call() throws Exception {
		return null;
	}
}