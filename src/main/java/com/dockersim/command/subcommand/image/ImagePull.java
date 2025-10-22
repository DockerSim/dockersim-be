package com.dockersim.command.subcommand.image;

import java.util.List;
import java.util.concurrent.Callable;

import com.dockersim.command.subcommand.ImageCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.service.image.DockerImageService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "pull")
@RequiredArgsConstructor
public class ImagePull implements Callable<CommandResult> {

	private final DockerImageService service;

	@CommandLine.ParentCommand
	private ImageCommand parent;

	@CommandLine.Option(names = {"-a", "--all-tags"}, description = "동일한 repo의 모든 Image를 다운로드합니다.")
	private boolean allTags;

	@CommandLine.Parameters(index = "0", description = "repo[:tag]")
	private String name;

	@Override
	public CommandResult call() throws Exception {
		List<DockerImageResponse> pull = service.pull(parent.getPrincipal(), name, allTags);
		return CommandResult.builder()
			.console(pull.stream()
				.flatMap(response -> response.getConsole().stream()).toList())
			.status(CommandResultStatus.CREATE)
			.changedImages(pull)
			.build();
	}
}