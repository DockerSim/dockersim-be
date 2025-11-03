package com.dockersim.command.aliases.container;

import java.util.List;
import java.util.concurrent.Callable;

import com.dockersim.command.DockerCommand;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.service.container.DockerContainerService;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "create")
@RequiredArgsConstructor
public class Create implements Callable<CommandResult> {

	private final DockerContainerService service;

	@CommandLine.ParentCommand
	private DockerCommand parent;

	@CommandLine.Option(names = "--name", description = "새로 생성할 Docker Container 이름")
	private String name;

	@CommandLine.Option(names = {"-p", "--publish"}, description = "호스트와 컨테이너 간 포트 설정")
	private List<String> port;

	@CommandLine.Option(names = {"-v", "--volume"}, description = "Container에 연결할 Volume과 경로")
	private List<String> volume;

	@CommandLine.Option(names = {"-e, --env"}, description = "환경 변수 설정 값")
	private List<String> env;

	@CommandLine.Parameters(index = "0", description = "Container을 만드는데 사용되는 Base Image")
	private String baseImageNameOrHexId;

	@Override
	public CommandResult call() throws Exception {
		return null;
	}
}
