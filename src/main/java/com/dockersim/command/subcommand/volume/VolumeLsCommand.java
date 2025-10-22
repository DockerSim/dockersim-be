package com.dockersim.command.subcommand.volume;

import java.util.concurrent.Callable;

import com.dockersim.command.subcommand.VolumeCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.service.volume.DockerVolumeService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "ls", aliases = "list")
@RequiredArgsConstructor
public class VolumeLsCommand implements Callable<CommandResult> {

	private final DockerVolumeService service;

	@CommandLine.ParentCommand
	private VolumeCommand parent;

	@CommandLine.Option(names = {"-q", "--quiet"}, description = "Docker Volume 이름만 출력")
	private boolean quiet;

	@Override
	public CommandResult call() throws Exception {
		return CommandResult.builder()
			.console(service.ls(parent.getPrincipal(), quiet))
			.status(CommandResultStatus.READ)
			.build();
	}
}