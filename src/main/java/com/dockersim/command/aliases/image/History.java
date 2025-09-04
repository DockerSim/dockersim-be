package com.dockersim.command.aliases.image;

import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import com.dockersim.command.DockerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.service.image.DockerImageService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "history")
@Component
@RequiredArgsConstructor
public class History implements Callable<CommandResult> {

	private final DockerImageService service;

	@CommandLine.ParentCommand
	private final DockerCommand parent;

	@CommandLine.Parameters(index = "0", description = "Docker Image 이름 또는 Hex ID")
	private String nameOrHexId;

	@Override
	public CommandResult call() throws Exception {
		return CommandResult.builder()
			.console(service.history(parent.getPrincipal(), nameOrHexId))
			.status(CommandResultStatus.READ)
			.build();
	}
}