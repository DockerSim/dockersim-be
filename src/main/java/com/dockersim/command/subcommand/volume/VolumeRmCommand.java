package com.dockersim.command.subcommand.volume;

import java.util.concurrent.Callable;

import com.dockersim.command.subcommand.VolumeCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerVolumeResponse;
import com.dockersim.service.volume.DockerVolumeService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "rm", aliases = "remove")
@RequiredArgsConstructor
public class VolumeRmCommand implements Callable<CommandResult> {

	private final DockerVolumeService service;

	@CommandLine.ParentCommand
	private VolumeCommand parent;

	@CommandLine.Parameters(index = "0", description = "삭제할 Docker Volume 이름 또는 Hex ID")
	private String nameOrHexId;

	@Override
	public CommandResult call() throws Exception {
		DockerVolumeResponse response = service.rm(parent.getPrincipal(), nameOrHexId);
		return CommandResult.builder()
			.console(response.getConsole())
			.status(CommandResultStatus.DELETE)
			.changedVolume(response)
			.build();
	}
}