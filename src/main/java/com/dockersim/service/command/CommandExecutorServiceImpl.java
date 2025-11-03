package com.dockersim.service.command;

import com.dockersim.command.DockerCommand;
import com.dockersim.config.SimulationUserPrincipal;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.ImageRemoveResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerCommandErrorCode;
import com.dockersim.parser.DockerCommandParser;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
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
    private final DockerCommandParser parser;

    @Override
    public CommandResult execute(String rawCommand, SimulationUserPrincipal principal) {
        if (rawCommand == null || !rawCommand.trim().toLowerCase().startsWith("docker")) {
            throw new BusinessException(DockerCommandErrorCode.INVALID_DOCKER_COMMAND, rawCommand);
        }

        List<String> tokenList = parser.tokenize(rawCommand);
        String[] args = tokenList.stream().skip(1).toArray(String[]::new);

        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);

        DockerCommand rootCommand = new DockerCommand();
        rootCommand.setPrincipal(principal);

        CommandLine cmd = new CommandLine(rootCommand, factory);

        cmd.setOut(printWriter);
        cmd.setErr(printWriter);

        cmd.execute(args);
        ParseResult parseResult = cmd.getParseResult();

        if (parseResult.isUsageHelpRequested() || parseResult.isVersionHelpRequested()) {
            String output = writer.toString();
            return CommandResult.builder()
                .console(Arrays.asList(output.split(System.lineSeparator())))
                .build();
        }

        List<CommandLine> commandChain = parseResult.asCommandLineList();
        CommandLine executedCommand = commandChain.get(commandChain.size() - 1);

        Object executionResult = executedCommand.getExecutionResult();

        if (executionResult instanceof CommandResult) {
            return (CommandResult) executionResult;
        }

        if (executionResult instanceof List) {
            List<?> resultList = (List<?>) executionResult;
            if (!resultList.isEmpty() && resultList.get(0) instanceof ImageRemoveResponse) {
                List<String> consoleOutput = new ArrayList<>();
                for (Object item : resultList) {
                    ImageRemoveResponse response = (ImageRemoveResponse) item;
                    if (response.getConsole() != null) {
                        consoleOutput.addAll(response.getConsole());
                    }
                }
                return CommandResult.builder()
                    .console(consoleOutput)
                    .build();
            }
        }

        return CommandResult.builder().build();
    }
}
