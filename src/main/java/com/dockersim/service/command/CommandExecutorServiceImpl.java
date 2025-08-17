package com.dockersim.service.command;

import com.dockersim.command.DockerCommand;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerCommandErrorCode;
import com.dockersim.executor.CommandResult;
import com.dockersim.parser.DockerCommandParser;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import picocli.CommandLine;
import picocli.CommandLine.ParseResult;


@Service
@RequiredArgsConstructor
public class CommandExecutorServiceImpl implements CommandExecutorService {

    private final CommandLine.IFactory factory;
    private final DockerCommand root;
    private final DockerCommandParser parser;

    @Override
    public CommandResult execute(String rawCommand) {
        if (rawCommand == null || !rawCommand.trim().toLowerCase().startsWith("docker")) {
            throw new BusinessException(DockerCommandErrorCode.INVALID_DOCKER_COMMAND, rawCommand);
        }

        // 명령어 토큰화
        List<String> tokenList = parser.tokenize(rawCommand);
        String[] args = tokenList.stream().skip(1).toArray(String[]::new);

        // Help 메시지를 캡처하기 위한 StringWriter 설정
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);

        CommandLine cmd = new CommandLine(root, factory);
        cmd.setOut(printWriter);
        cmd.setErr(printWriter);

        // 명령어 실행
        cmd.execute(args);
        ParseResult parseResult = cmd.getParseResult();

        // help나 version 옵션이 사용된 경우
        if (parseResult.isUsageHelpRequested() || parseResult.isVersionHelpRequested()) {
            String output = writer.toString();
            return CommandResult.builder()
                .console(Arrays.asList(output.split(System.lineSeparator())))
                .build();
        }

        // 일반적인 명령어 실행 결과 처리
        List<CommandLine> commandChain = parseResult.asCommandLineList();
        CommandLine executedCommand = commandChain.get(commandChain.size() - 1);

        return executedCommand.getExecutionResult();
    }
}
