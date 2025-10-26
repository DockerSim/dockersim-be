package com.dockersim.command.subcommand.network;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import com.dockersim.command.subcommand.NetworkCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerNetworkResponse;
import com.dockersim.service.network.DockerNetworkService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "prune", description = "기본동작: 참조되지 않는 익명 볼륨 삭제")
@RequiredArgsConstructor
public class NetworkPruneCommand implements Callable<CommandResult> {

	private final DockerNetworkService service;

	@CommandLine.ParentCommand
	private NetworkCommand parent;

	@Override
	public CommandResult call() throws Exception {
		List<DockerNetworkResponse> networks = service.prune(parent.getPrincipal());
		return CommandResult.builder()
			.console(
				Stream.concat(
					Stream.of(networks.isEmpty()
						? "[사용되지 않는 네트워크가 없습니다.]"
						: "Deleted Networks:"),
					networks.stream()
						.flatMap(response -> response.getConsole().stream())
				).toList()
			)
			.status(CommandResultStatus.DELETE)
			.changedNetworks(networks)
			.build();
	}
}
