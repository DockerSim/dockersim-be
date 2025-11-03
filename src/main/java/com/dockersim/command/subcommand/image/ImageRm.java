package com.dockersim.command.subcommand.image;

import java.util.concurrent.Callable;

import com.dockersim.command.subcommand.ImageCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.service.image.DockerImageService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "rm", aliases = "remove")
@RequiredArgsConstructor
public class ImageRm implements Callable<CommandResult> {

	private final DockerImageService service;

	@CommandLine.ParentCommand
	private ImageCommand parent;

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
