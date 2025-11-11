package com.dockersim.service.command;

import com.dockersim.command.DockerCommand;
import com.dockersim.config.SimulationUserPrincipal;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.dto.response.ImageRemoveResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerCommandErrorCode;
import com.dockersim.parser.DockerCommandParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import picocli.CommandLine;
import picocli.CommandLine.ParseResult;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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

        System.out.println(Arrays.toString(args));
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);

        DockerCommand rootCommand = new DockerCommand();
        rootCommand.setPrincipal(principal);

        CommandLine cmd = new CommandLine(rootCommand, factory);

        final AtomicReference<Throwable> executionException = new AtomicReference<>();

        cmd.setExecutionExceptionHandler((ex, commandLine, parseResult) -> {
            if (ex instanceof picocli.CommandLine.ExecutionException && ex.getCause() != null) {
                executionException.set(ex.getCause());
            } else {
                executionException.set(ex);
            }
            return commandLine.getCommandSpec().exitCodeOnExecutionException();
        });

        cmd.setOut(printWriter);
        cmd.setErr(printWriter);

        cmd.execute(args);

        if (executionException.get() != null) {
            Throwable caught = executionException.get();

            BusinessException business = unwrapBusinessException(caught);
            if (business != null) {
                throw business;
            }
            if (caught instanceof RuntimeException) {
                throw (RuntimeException) caught;
            }
            throw new RuntimeException("Command execution failed", caught);
        }

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
    private BusinessException unwrapBusinessException(Throwable t) {
        int depth = 0;
        final int MAX_DEPTH = 20;
        while (t != null && depth++ < MAX_DEPTH) {
            if (t instanceof BusinessException) {
                return (BusinessException) t;
            }
            t = t.getCause();
        }
        return null;
    }
}
