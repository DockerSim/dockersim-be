package com.dockersim.command.aliases.container;

import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import com.dockersim.command.DockerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.service.container.DockerContainerService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "rm")
@Component
@RequiredArgsConstructor
public class Rm implements Callable<CommandResult> {

	private final DockerContainerService service;

	@CommandLine.ParentCommand
	private final DockerCommand parent;

	@CommandLine.Option(names = {"-f", "--force"}, description = "Running 상태가 아닌 Container의 삭제 여부")
	private boolean force;

	@CommandLine.Parameters(index = "0", description = "삭제할 Container 이름 또는 ID")
	private String nameOrHexId;

	@Override
	public CommandResult call() throws Exception {
		return null;
	}
}
