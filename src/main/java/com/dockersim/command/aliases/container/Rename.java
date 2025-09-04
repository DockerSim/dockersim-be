package com.dockersim.command.aliases.container;

import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import com.dockersim.command.DockerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.service.container.DockerContainerService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "rename")
@Component
@RequiredArgsConstructor
public class Rename implements Callable<CommandResult> {

	private final DockerContainerService service;

	@CommandLine.ParentCommand
	private final DockerCommand parent;

	@CommandLine.Parameters(index = "0", description = "이름을 변경할 Container의 이름 또는 Id")
	private String nameOrHexId;

	@CommandLine.Parameters(index = "1", description = "Container의 새로운 이름")
	private String newName;

	@Override
	public CommandResult call() throws Exception {
		return null;
	}
}
