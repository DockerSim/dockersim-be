package com.dockersim.command.subcommand.volume;

import java.util.concurrent.Callable;

import com.dockersim.command.subcommand.VolumeCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.service.volume.DockerVolumeService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "inspect")
@RequiredArgsConstructor
public class VolumeInspectCommand implements Callable<CommandResult> {

	private final DockerVolumeService service;

	@CommandLine.ParentCommand
	private VolumeCommand parent;

	@CommandLine.Parameters(index = "0", description = "조회할 Docker Volume 이름")
	private String name;

	@Override
	public CommandResult call() throws Exception {
		return CommandResult.builder()
			.console(service.inspect(parent.getPrincipal(), name))
			.status(CommandResultStatus.READ)
			.build();
	}
}
