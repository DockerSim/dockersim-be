package com.dockersim.command.subcommand.network;

import java.util.concurrent.Callable;

import com.dockersim.command.subcommand.NetworkCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.service.network.DockerNetworkService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "inspect")
@RequiredArgsConstructor
public class NetworkInspectCommand implements Callable<CommandResult> {

	private final DockerNetworkService service;

	@CommandLine.ParentCommand
	private NetworkCommand parent;

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
