package com.dockersim.command.subcommand.network;

import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import com.dockersim.command.subcommand.NetworkCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerVolumeResponse;
import com.dockersim.service.network.DockerNetworkService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "rm", aliases = "remove")
@Component
@RequiredArgsConstructor
public class NetworkRmCommand implements Callable<CommandResult> {

	private final DockerNetworkService service;

	@CommandLine.ParentCommand
	private final NetworkCommand parent;

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