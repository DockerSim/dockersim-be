package com.dockersim.command.aliases.container;

import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import com.dockersim.command.DockerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.service.container.DockerContainerService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "ps")
@Component
@RequiredArgsConstructor
public class Ps implements Callable<CommandResult> {

	private final DockerContainerService dockerContainerService;

	@CommandLine.ParentCommand
	private final DockerCommand parent;

	@CommandLine.Option(names = {"-a", "--all"}, description = "Existed 상태의 Container 출력 여부")
	private boolean all = false;

	@CommandLine.Option(names = {"-q", "--quiet"}, description = "Container ID만 출력 여부")
	private boolean quiet = false;

	@Override
	public CommandResult call() {
		return null;
	}
}