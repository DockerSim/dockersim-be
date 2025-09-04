package com.dockersim.command.subcommand.container;

import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import com.dockersim.command.subcommand.ContainerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.service.container.DockerContainerService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "port")
@Component
@RequiredArgsConstructor
public class ContainerPort implements Callable<CommandResult> {

	private final DockerContainerService service;

	@CommandLine.ParentCommand
	private final ContainerCommand parent;

	@CommandLine.Parameters(index = "0", description = "매핑 포트 확인??")
	private String port;

	@Override
	public CommandResult call() throws Exception {
		return null;
	}
}
