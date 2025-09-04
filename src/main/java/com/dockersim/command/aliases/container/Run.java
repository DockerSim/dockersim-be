package com.dockersim.command.aliases.container;

import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import com.dockersim.command.DockerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.service.container.DockerContainerService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "run")
@Component
@RequiredArgsConstructor
public class Run implements Callable<CommandResult> {

	private final DockerContainerService service;

	@CommandLine.ParentCommand
	private final DockerCommand parent;

	@CommandLine.Option(names = {"-i", "--interactice"})
	private boolean interactice;

	@CommandLine.Option(names = {"-t", "--tty"})
	private boolean tty;

	@CommandLine.Option(names = "-d", description = "메인 터미널에서 나올 경우 메인 터미널을 종료하지 않고 나오는 옵션")
	private boolean d;

	@CommandLine.Parameters(index = "0", description = "만들고, 실행할 Container의 이름 또는 ID")
	private String nameOrHexId;

	@Override
	public CommandResult call() throws Exception {
		return null;
	}
}
