package com.dockersim.command.aliases.container;

import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import com.dockersim.command.DockerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.service.container.DockerContainerService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "unpause")
@Component
@RequiredArgsConstructor
public class Unpause implements Callable<CommandResult> {

	private final DockerContainerService service;

	@CommandLine.ParentCommand
	private final DockerCommand parent;

	@CommandLine.Parameters(index = "0", description = "일시정지 상태를 해제할 Container 이름 또는 Hex Id")
	private String nameOrHexId;

	@Override
	public CommandResult call() throws Exception {
		return null;
	}
}
