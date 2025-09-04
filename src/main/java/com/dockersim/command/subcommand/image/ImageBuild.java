package com.dockersim.command.subcommand.image;

import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import com.dockersim.command.subcommand.ImageCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.service.image.DockerImageService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "build")
@Component
@RequiredArgsConstructor
public class ImageBuild implements Callable<CommandResult> {

	private final DockerImageService service;

	@CommandLine.ParentCommand
	private final ImageCommand parent;

	@CommandLine.Option(names = {"-t", "--tag"}, description = "새로 생성하는 Docker Image 이름")
	private String name;

	@CommandLine.Parameters(index = "0", description = "DockerFile 경로")
	private String path;

	@Override
	public CommandResult call() throws Exception {
		DockerImageResponse response = service.build(parent.getPrincipal(), path, name);
		return CommandResult.builder()
			.console(response.getConsole())
			.status(CommandResultStatus.CREATE)
			.changedImage(response)
			.build();
	}
}