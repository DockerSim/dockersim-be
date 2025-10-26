package com.dockersim.command.subcommand.network;

import java.util.concurrent.Callable;

import com.dockersim.command.subcommand.NetworkCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerNetworkResponse;
import com.dockersim.service.network.DockerNetworkService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "create")
@RequiredArgsConstructor
public class NetworkCreateCommand implements Callable<CommandResult> {

	private final DockerNetworkService service;

	@CommandLine.ParentCommand
	private NetworkCommand parent;

	@CommandLine.Parameters(index = "0", description = "새로 생성 할 네트워크 이름")
	private String networkName;

	@Override
	public CommandResult call() throws Exception {
		DockerNetworkResponse network = service.create(parent.getPrincipal(), networkName);
		return CommandResult.builder()
			.console(network.getConsole())
			.status(CommandResultStatus.CREATE)
			.changedNetwork(network)
			.build();
	}
}
