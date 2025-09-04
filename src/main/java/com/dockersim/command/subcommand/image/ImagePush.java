package com.dockersim.command.subcommand.image;

import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import com.dockersim.command.subcommand.ImageCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.service.image.DockerImageService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "push")
@Component
@RequiredArgsConstructor
public class ImagePush implements Callable<CommandResult> {

	private final DockerImageService service;

	@CommandLine.ParentCommand
	private final ImageCommand parent;

	@CommandLine.Parameters(index = "0", description = "Docker Image 이름")
	private String name;

	@Override
	public CommandResult call() throws Exception {
		DockerImageResponse response = service.push(parent.getPrincipal(), name);
		return CommandResult.builder()
			.console(response.getConsole())
			.status(CommandResultStatus.CREATE)
			.changedImage(response)
			.build();
	}
}
