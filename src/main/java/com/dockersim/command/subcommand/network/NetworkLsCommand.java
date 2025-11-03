package com.dockersim.command.subcommand.network;

import java.util.concurrent.Callable;

import com.dockersim.command.subcommand.NetworkCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.service.network.DockerNetworkService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "ls", aliases = "list")
@RequiredArgsConstructor
public class NetworkLsCommand implements Callable<CommandResult> {

	private final DockerNetworkService service;

	@CommandLine.ParentCommand
	private NetworkCommand parent;
	
	@CommandLine.Option(names = {"-q", "--quiet"}, description = "네트워크 이름만 출력")
	private boolean quiet;

	@Override
	public CommandResult call() throws Exception {
		return CommandResult.builder()
			.console(service.ls(parent.getPrincipal(), quiet))
			.status(CommandResultStatus.READ)
			.build();
	}
}