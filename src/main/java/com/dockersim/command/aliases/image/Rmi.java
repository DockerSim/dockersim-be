package com.dockersim.command.aliases.image;

import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import com.dockersim.command.DockerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.service.image.DockerImageService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "rmi")
@Component
@RequiredArgsConstructor
public class Rmi implements Callable<CommandResult> {

	private final DockerImageService service;

	@CommandLine.ParentCommand
	private final DockerCommand parent;

	@CommandLine.Option(names = {"-f", "--force"}, description = "해당 Image가 다른 Container의 Base Image이여도 삭제합니다.")
	private boolean force;

	@CommandLine.Parameters(index = "0", description = "repo[:tag] | Hex ID")
	private String nameOrHexId;

	@Override
	public CommandResult call() throws Exception {
		DockerImageResponse response = service.rm(parent.getPrincipal(), nameOrHexId, force);
		return CommandResult.builder()
			.console(response.getConsole())
			.status(CommandResultStatus.DELETE)
			.changedImage(response)
			.build();
	}
}
