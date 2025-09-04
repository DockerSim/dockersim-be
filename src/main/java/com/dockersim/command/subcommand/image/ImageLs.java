package com.dockersim.command.subcommand.image;

import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import com.dockersim.command.subcommand.ImageCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.service.image.DockerImageService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "ls", aliases = "list")
@Component
@RequiredArgsConstructor
public class ImageLs implements Callable<CommandResult> {

	private final DockerImageService service;

	@CommandLine.ParentCommand
	private final ImageCommand parent;

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