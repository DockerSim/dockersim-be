package com.dockersim.command.subcommand.container;

import java.util.concurrent.Callable;

import com.dockersim.command.subcommand.ContainerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.service.container.DockerContainerService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "remove")
@RequiredArgsConstructor
public class ContainerRm implements Callable<CommandResult> {

	private final DockerContainerService service;

	@CommandLine.ParentCommand
	private ContainerCommand parent;

	@CommandLine.Option(names = {"-f", "--force"}, description = "Running 상태가 아닌 Container의 삭제 여부")
	private boolean force;

	@CommandLine.Parameters(index = "0", description = "삭제할 Container 이름 또는 ID")
	private String nameOrHexId;

	@Override
	public CommandResult call() throws Exception {
		return null;
	}
}
