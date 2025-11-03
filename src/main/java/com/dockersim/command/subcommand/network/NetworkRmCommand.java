package com.dockersim.command.subcommand.network;

import java.util.concurrent.Callable;

import com.dockersim.command.subcommand.NetworkCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerNetworkResponse;
import com.dockersim.service.network.DockerNetworkService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "rm", aliases = "remove")
@RequiredArgsConstructor
public class NetworkRmCommand implements Callable<CommandResult> {

	private final DockerNetworkService service;

	@CommandLine.ParentCommand
	private NetworkCommand parent;

	@CommandLine.Parameters(index = "0", description = "상제 조회할 네트워크 이름 또는 Hex ID")
	private String networkNameOrHexId;

	@Override
	public CommandResult call() throws Exception {
		DockerNetworkResponse response = service.rm(parent.getPrincipal(), networkNameOrHexId);
		return CommandResult.builder()
			.console(response.getConsole())
			.status(CommandResultStatus.DELETE)
			.changedNetwork(response)
			.build();
	}
}