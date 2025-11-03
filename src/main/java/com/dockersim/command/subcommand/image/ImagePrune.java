package com.dockersim.command.subcommand.image;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import com.dockersim.command.subcommand.ImageCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.service.image.DockerImageService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "prune")
@RequiredArgsConstructor
public class ImagePrune implements Callable<CommandResult> {

	private final DockerImageService service;

	@CommandLine.ParentCommand
	private ImageCommand parent;

	@CommandLine.Option(names = {"-a", "--all"}, description = "댕글링 이미지/참조 여부에 관계없이 삭제합니다.")
	private boolean all;

	@Override
	public CommandResult call() throws Exception {
		List<DockerImageResponse> prune = service.prune(parent.getPrincipal(), all);
		if (prune == null) {
			return CommandResult.builder()
				.console(List.of("삭제할 이미지가 없습니다."))
				.status(CommandResultStatus.READ)
				.build();
		}
		return CommandResult.builder()
			.console(Stream.concat(Stream.of("Deleted Images:"),
					prune.stream().flatMap(response -> response.getConsole().stream()))
				.toList()
			)
			.status(CommandResultStatus.DELETE)
			.changedImages(prune)
			.build();
	}
}