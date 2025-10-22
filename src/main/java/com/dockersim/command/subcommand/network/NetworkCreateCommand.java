package com.dockersim.command.subcommand.network;

import java.util.concurrent.Callable;

import com.dockersim.command.subcommand.NetworkCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerVolumeResponse;
import com.dockersim.service.network.DockerNetworkService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "create")
@RequiredArgsConstructor
public class NetworkCreateCommand implements Callable<CommandResult> {

	private final DockerNetworkService service;

	@CommandLine.ParentCommand
	private NetworkCommand parent;

	@CommandLine.Parameters(index = "0", description = "새로 생성하는 Docker Volume 이름")
	private String name;

	@Override
	public CommandResult call() throws Exception {
		DockerVolumeResponse volume = service.create(parent.getPrincipal(), name, false);
		return CommandResult.builder()
			.console(volume.getConsole())
			.status(CommandResultStatus.CREATE)
			.changedVolume(volume)
			.build();
	}
}
