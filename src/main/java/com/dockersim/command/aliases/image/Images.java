package com.dockersim.command.aliases.image;

import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import com.dockersim.command.DockerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.service.image.DockerImageService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "images")
@Component
@RequiredArgsConstructor
public class Images implements Callable<CommandResult> {

	private final DockerImageService service;

	@CommandLine.ParentCommand
	private final DockerCommand parent;

	@CommandLine.Option(names = {"-a", "--all"}, description = "댕글링 이미지를 포함합니다.")
	private boolean all;

	@CommandLine.Option(names = {"-q", "--quiet"}, description = "Hex ID만 출력합니다.")
	private boolean quiet;

	@Override
	public CommandResult call() throws Exception {
		return CommandResult.builder()
			.console(service.ls(parent.getPrincipal(), all, quiet))
			.status(CommandResultStatus.READ)
			.build();
	}
}