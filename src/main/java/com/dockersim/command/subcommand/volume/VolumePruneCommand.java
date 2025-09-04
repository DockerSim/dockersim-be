package com.dockersim.command.subcommand.volume;

import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import com.dockersim.command.subcommand.VolumeCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.CommandResultStatus;
import com.dockersim.dto.response.DockerVolumeResponse;
import com.dockersim.service.volume.DockerVolumeService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "prune", description = "기본동작: 참조되지 않는 익명 볼륨 삭제")
@Component
@RequiredArgsConstructor
public class VolumePruneCommand implements Callable<CommandResult> {

	private final DockerVolumeService service;

	@CommandLine.ParentCommand
	private final VolumeCommand parent;

	@CommandLine.Option(names = {"-a", "--all"}, description = "기본 동작 변경: 참조되지 않은 익명/명명 볼륨 삭제")
	private boolean all;

	@Override
	public CommandResult call() throws Exception {
		List<DockerVolumeResponse> prune = service.prune(parent.getPrincipal(), all);
		return CommandResult.builder()
			.console(prune.stream()
				.flatMap(response -> response.getConsole().stream()).toList())
			.status(CommandResultStatus.DELETE)
			.changedVolumes(prune)
			.build();
	}
}
