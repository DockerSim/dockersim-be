package com.dockersim.command.aliases.image;

import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import com.dockersim.command.DockerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.service.image.DockerImageService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "pull")
@Component
@RequiredArgsConstructor
public class Pull implements Callable<CommandResult> {

	private final DockerImageService service;

	@CommandLine.ParentCommand
	private final DockerCommand parent;

	@CommandLine.Option(names = {"-a", "--all"}, description = "동일한 repo의 모든 Image를 다운로드합니다.")
	private boolean all;

	@CommandLine.Parameters(index = "0", description = "repo[:tag]")
	private String name;

	@Override
	public CommandResult call() throws Exception {
		List<DockerImageResponse> pull = service.pull(parent.getPrincipal(), name, all);
		return CommandResult.builder()
			.console(pull.stream()
				.flatMap(response -> response.getConsole().stream()).toList())
			.status(CommandResultStatus.CREATE)
			.changedImages(pull)
			.build();
	}
}
