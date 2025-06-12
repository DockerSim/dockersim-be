package com.dockersim.parser;

import com.dockersim.dto.ParsedDockerCommand;
import com.dockersim.parser.tokenizer.AdvancedDockerTokenizer;
import com.dockersim.parser.validator.CommandValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DockerCommandParser
 *
 * Parses a Docker-like command string into a structured ParsedDockerCommand.
 * Responsible for:
 * - Tokenizing the input string
 * - Determining the main command, group, subcommand, options, flags, and
 * arguments
 * - Validating the parsed command
 * - Extracting special values (e.g., container name, image name and tag)
 *
 * Example:
 * Input: docker run --name my-container nginx:latest
 * Output: ParsedDockerCommand with group=container, subcommand=run,
 * imageName=nginx, imageTag=latest, etc.
 */
@Component
@Slf4j
public class DockerCommandParser {
    /*
     * Pattern to extract image name and optional tag from a Docker image string.
     * Examples:
     * - "nginx" -> group(1)=nginx, group(2)=null
     * - "nginx:latest" -> group(1)=nginx, group(2)=latest
     * - "redis:6.2-alpine" -> group(1)=redis, group(2)=6.2-alpine
     * 
     * Regex breakdown:
     * ^ : start of string
     * ([^:]+) : group(1), one or more characters that are not ':', representing the
     * image name
     * (?::(.+))? : non-capturing group, optional:
     * ':' followed by one or more characters (group(2), the tag)
     * $ : end of string
     */
    private static final Pattern IMAGE_TAG_PATTERN = Pattern.compile("^([^:]+)(?::(.+))?$");
    private static final List<String> GROUP_COMMANDS = Arrays.asList("container", "image", "network", "volume",
            "system");
    private static final List<String> MULTI_VALUE_OPTIONS = Arrays.asList("-e", "-v", "-p", "--env", "--volume",
            "--publish");
    private static final List<String> KNOWN_FLAGS = Arrays.asList("-d", "-i", "-it", "-a", "--all");

    private final AdvancedDockerTokenizer tokenizer;
    private final CommandValidator validator;

    public DockerCommandParser() {
        this.tokenizer = new AdvancedDockerTokenizer();
        this.validator = new CommandValidator();
    }

    /**
     * Main entry point: parse the input command.
     */
    public ParsedDockerCommand parse(String command) {
        log.debug("Parsing command: {}", command);

        if (isBlank(command)) {
            return errorResult(command, "명령어가 비어있습니다.");
        }

        try {
            List<String> tokens = tokenizer.tokenize(command.trim());
            return parseTokens(command, tokens);
        } catch (Exception e) {
            log.error("명령어 파싱 중 오류 발생", e);
            return errorResult(command, "명령어 파싱 중 오류: " + e.getMessage());
        }
    }

    private ParsedDockerCommand parseTokens(String command, List<String> tokens) {
        if (tokens.isEmpty()) {
            return errorResult(command, "명령어가 비어있습니다.");
        }
        if (!"docker".equals(tokens.get(0))) {
            return errorResult(command, "명령어는 'docker'로 시작해야 합니다.");
        }
        if (tokens.size() == 1) {
            return errorResult(command, "docker 뒤에 실행할 명령어를 입력해주세요. 예: docker ps");
        }

        return parseDockerCommand(command, tokens);
    }

    private ParsedDockerCommand parseDockerCommand(String command, List<String> tokens) {
        ParsedDockerCommand.ParsedDockerCommandBuilder builder = ParsedDockerCommand.builder()
                .command(command)
                .mainCommand("docker")
                .valid(true);

        int currentIndex = 1;
        String firstToken = tokens.get(currentIndex);

        if (isGroupCommand(firstToken)) {
            builder.group(firstToken);
            currentIndex++;

            if (currentIndex >= tokens.size()) {
                return errorResult(command, firstToken + " 뒤에 실행할 명령어를 입력해주세요.");
            }
            builder.subCommand(tokens.get(currentIndex));
            currentIndex++;
        } else {
            builder.subCommand(firstToken);
            builder.group(getCommandGroup(firstToken));
            currentIndex++;
        }

        ParseResult parseResult = parseOptionsAndArguments(tokens, currentIndex);

        builder.options(parseResult.options)
                .multiOptions(parseResult.multiOptions)
                .flags(parseResult.flags)
                .arguments(parseResult.arguments);

        extractSpecialValues(builder, parseResult);

        ParsedDockerCommand result = builder.build();
        String validationError = validator.validate(result);

        if (validationError != null) {
            return errorResult(command, validationError);
        }

        return result;
    }

    private void extractSpecialValues(ParsedDockerCommand.ParsedDockerCommandBuilder builder, ParseResult parseResult) {
        String containerName = parseResult.options.get("--name");
        if (containerName != null) {
            builder.containerName(containerName);
        }

        String network = parseResult.options.get("--network");
        if (network != null) {
            builder.networkName(network);
        }

        if (isRunCommand(builder) && !parseResult.arguments.isEmpty()) {
            parseImageNameAndTag(builder, parseResult.arguments.get(0));
        }
    }

    private boolean isRunCommand(ParsedDockerCommand.ParsedDockerCommandBuilder builder) {
        return "run".equals(builder.build().getSubCommand());
    }

    private void parseImageNameAndTag(ParsedDockerCommand.ParsedDockerCommandBuilder builder, String imageArg) {
        Matcher matcher = IMAGE_TAG_PATTERN.matcher(imageArg);
        if (matcher.matches()) {
            builder.imageName(matcher.group(1));
            builder.imageTag(Optional.ofNullable(matcher.group(2)).orElse("latest"));
        }
    }

    /**
     * Parse options, multi-options, flags, and plain arguments from tokens.
     */
    private ParseResult parseOptionsAndArguments(List<String> tokens, int startIndex) {
        Map<String, String> options = new HashMap<>();
        Map<String, List<String>> multiOptions = new HashMap<>();
        List<String> flags = new ArrayList<>();
        List<String> arguments = new ArrayList<>();

        for (int i = startIndex; i < tokens.size(); i++) {
            String token = tokens.get(i);

            if (token.startsWith("--")) {
                parseLongOption(tokens, options, flags, i);
            } else if (token.startsWith("-") && !token.equals("-")) {
                i = parseShortOption(tokens, multiOptions, options, flags, i);
            } else {
                arguments.add(token);
            }
        }

        return new ParseResult(options, multiOptions, flags, arguments);
    }

    private void parseLongOption(List<String> tokens, Map<String, String> options, List<String> flags, int i) {
        String token = tokens.get(i);
        if (token.contains("=")) {
            String[] parts = token.split("=", 2);
            options.put(parts[0], parts[1]);
        } else if (hasNextNonFlagToken(tokens, i)) {
            options.put(token, tokens.get(i + 1));
        } else {
            flags.add(token);
        }
    }

    private int parseShortOption(List<String> tokens, Map<String, List<String>> multiOptions,
            Map<String, String> options, List<String> flags, int i) {
        String token = tokens.get(i);

        if (isMultiValueOption(token) && hasNextNonFlagToken(tokens, i)) {
            multiOptions.computeIfAbsent(token, k -> new ArrayList<>()).add(tokens.get(++i));
        } else if (isKnownFlag(token)) {
            flags.add(token);
        } else if (hasNextNonFlagToken(tokens, i)) {
            options.put(token, tokens.get(++i));
        } else {
            flags.add(token);
        }
        return i;
    }

    private boolean hasNextNonFlagToken(List<String> tokens, int i) {
        return i + 1 < tokens.size() && !tokens.get(i + 1).startsWith("-");
    }

    private boolean isGroupCommand(String command) {
        return GROUP_COMMANDS.contains(command);
    }

    private boolean isMultiValueOption(String option) {
        return MULTI_VALUE_OPTIONS.contains(option);
    }

    private boolean isKnownFlag(String option) {
        return KNOWN_FLAGS.contains(option);
    }

    private String getCommandGroup(String command) {
        switch (command) {
            case "run":
            case "start":
            case "stop":
            case "restart":
            case "rm":
            case "ps":
            case "exec":
            case "logs":
                return "container";
            case "pull":
            case "push":
            case "images":
            case "rmi":
            case "build":
            case "tag":
            case "inspect":
                return "image";
            case "network":
                return "network";
            case "volume":
                return "volume";
            default:
                return "unknown";
        }
    }

    private ParsedDockerCommand errorResult(String command, String errorMessage) {
        return ParsedDockerCommand.builder()
                .command(command)
                .valid(false)
                .errorMessage(errorMessage)
                .build();
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private static class ParseResult {
        final Map<String, String> options;
        final Map<String, List<String>> multiOptions;
        final List<String> flags;
        final List<String> arguments;

        ParseResult(Map<String, String> options, Map<String, List<String>> multiOptions,
                List<String> flags, List<String> arguments) {
            this.options = options;
            this.multiOptions = multiOptions;
            this.flags = flags;
            this.arguments = arguments;
        }
    }
}
