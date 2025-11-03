package com.dockersim.command.subcommand.image;

import java.util.concurrent.Callable;

import com.dockersim.command.subcommand.ImageCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.service.image.DockerImageService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "history")
@RequiredArgsConstructor
public class ImageHistory implements Callable<CommandResult> {

	private final DockerImageService service;

	@CommandLine.ParentCommand
	private ImageCommand parent;

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