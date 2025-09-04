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

@CommandLine.Command(name = "rm", aliases = "remove")
@Component
@RequiredArgsConstructor
public class ImageRm implements Callable<CommandResult> {

	private final DockerImageService service;

	@CommandLine.ParentCommand
	private final ImageCommand parent;

	@CommandLine.Option(names = {"-f", "--force"},
		description = "기본 동작 변경: 삭제하려는 Docker Image 를 기반으로 생성된 Docker Container 가 있어도 Docker Image 삭제")
	private boolean all;

	@CommandLine.Parameters(index = "0", description = "도커 이미지 이름 또는 Hex ID")
	private String nameOrHexId;

	@Override
	public CommandResult call() throws Exception {
		DockerImageResponse response = service.rm(parent.getPrincipal(), nameOrHexId, all);
		return CommandResult.builder()
			.console(response.getConsole())
			.status(CommandResultStatus.DELETE)
			.changedImage(response)
			.build();
	}
}
