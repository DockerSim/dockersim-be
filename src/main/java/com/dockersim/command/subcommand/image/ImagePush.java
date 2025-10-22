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

@CommandLine.Command(name = "push")
@RequiredArgsConstructor
public class ImagePush implements Callable<CommandResult> {

	private final DockerImageService service;

	@CommandLine.ParentCommand
	private ImageCommand parent;

	@CommandLine.Option(names = {"-a", "--all-tags"}, description = "동일한 repo의 모든 Image를 업로드합니다.")
	private boolean allTags;

	@CommandLine.Parameters(index = "0", description = "namespace/repo[:tag]")
	private String name;

	@Override
	public CommandResult call() throws Exception {
		List<DockerImageResponse> response = service.push(parent.getPrincipal(), name, allTags);
		return CommandResult.builder()
			.console(null)
			.status(CommandResultStatus.CREATE)
			.changedImages(response)
			.build();
	}
}
