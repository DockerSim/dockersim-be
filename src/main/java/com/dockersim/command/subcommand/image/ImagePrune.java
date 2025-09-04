package com.dockersim.command.subcommand.image;

import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import com.dockersim.command.subcommand.ImageCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.service.image.DockerImageService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "prune")
@Component
@RequiredArgsConstructor
public class ImagePrune implements Callable<CommandResult> {

	private final DockerImageService service;

	@CommandLine.ParentCommand
	private final ImageCommand parent;

	@CommandLine.Option(names = {"-a", "--all"}, description = "기본 동작 변경: 컨테이너에 연결되지 않는 모든 이미지 삭제")
	private boolean all;

	@Override
	public CommandResult call() throws Exception {
		List<DockerImageResponse> prune = service.prune(parent.getPrincipal(), all);
		return CommandResult.builder()
			.console(prune.stream()
				.flatMap(response -> response.getConsole().stream()).toList())
			.status(CommandResultStatus.DELETE)
			.changedImages(prune)
			.build();
	}
}