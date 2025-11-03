package com.dockersim.command.subcommand.network;

import java.util.concurrent.Callable;

import com.dockersim.command.subcommand.NetworkCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerNetworkResponse;
import com.dockersim.service.network.DockerNetworkService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "connect")
@RequiredArgsConstructor
public class NetworkConnectCommand implements Callable<CommandResult> {

	private final DockerNetworkService service;

	@CommandLine.ParentCommand
	private NetworkCommand parent;

	@CommandLine.Parameters(index = "0", description = "연결할 네트워크 이름 또는 Hex ID")
	private String networkNameOrHexId;

	@CommandLine.Parameters(index = "1", description = "연결할 컨테이너 이름 또는 Hex ID")
	private String containerNameOrHexId;

	@Override
	public CommandResult call() throws Exception {
		DockerNetworkResponse network = service.connect(parent.getPrincipal(),
			networkNameOrHexId, containerNameOrHexId);
		return CommandResult.builder()
			.console(network.getConsole())
			.status(CommandResultStatus.UPDATE)
			.changedNetwork(network)
			.build();
	}
}
