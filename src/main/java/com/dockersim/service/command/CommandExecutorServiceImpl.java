package com.dockersim.service.command;

import com.dockersim.command.DockerCommand;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerCommandErrorCode;
import com.dockersim.executor.CommandResult;
import com.dockersim.parser.DockerCommandParser;
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

        // 2. 따옴표를 처리하는 CommandParser를 사용하여 안전하게 토큰화
        List<String> tokenList = parser.tokenize(rawCommand);
        String[] args = tokenList.stream().skip(1).toArray(String[]::new);

        // 3. Picocli 실행 및 결과 처리
        CommandLine cmd = new CommandLine(root, factory);
        cmd.execute(args);

        ParseResult parseResult = cmd.getParseResult();

        List<CommandLine> commandChain = parseResult.asCommandLineList();

        CommandLine executedCommand = commandChain.get(commandChain.size() - 1);

        return executedCommand.getExecutionResult();
    }

    /**
     * ParseResult 계층 구조를 탐색하여 가장 마지막(가장 깊은) 하위 명령어의 실행 결과를 찾아 반환합니다.
     *
     * @param parseResult 최상위 파싱 결과
     * @return 최종 실행 결과 (CommandResult)
     */
    private CommandResult findExecutionResult(ParseResult parseResult) {
        ParseResult current = parseResult;
        while (current.hasSubcommand()) {
            current = current.subcommand();
        }
        // 가장 깊은 곳에 있는 명령어의 실행 결과를 가져옴
        return current.commandSpec().commandLine().getExecutionResult();
    }
}
