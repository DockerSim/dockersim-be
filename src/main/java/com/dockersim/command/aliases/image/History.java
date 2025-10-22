package com.dockersim.command.aliases.image;

import java.util.concurrent.Callable;

import com.dockersim.command.DockerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.service.image.DockerImageService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "history")
@RequiredArgsConstructor
public class History implements Callable<CommandResult> {

	private final DockerImageService service;

	@CommandLine.ParentCommand
	private DockerCommand parent;

	@CommandLine.Parameters(index = "0", description = "repo[:tag] | Hex ID")
	private String nameOrHexId;

	@Override
	public CommandResult call() throws Exception {
		return CommandResult.builder()
			.console(service.history(parent.getPrincipal(), nameOrHexId))
			.status(CommandResultStatus.READ)
			.build();
	}
}