package com.dockersim.command.aliases.container;

import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import com.dockersim.command.DockerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.service.container.DockerContainerService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

@Command(name = "attach")
@Component
@RequiredArgsConstructor
public class Attach implements Callable<CommandResult> {

	private final DockerContainerService service;

	@ParentCommand
	private final DockerCommand parent;

	@Parameters(index = "0", description = "표준 터미널에 접속할 실행 중인 Docker Container 이름 또는 ID")
	private String nameOrHexId;

	@Override
	public CommandResult call() {
		return null;
	}
}