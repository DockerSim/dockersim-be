package com.dockersim.command;

import com.dockersim.command.subcommand.ImageCommand;
import com.dockersim.command.toplevel.ImagesCommand;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "docker", mixinStandardHelpOptions = true,
    subcommands = {
        // 1. Subcommands with objects (e.g., docker image ls)
        ImageCommand.class,

        // 2. Top-level commands (e.g., docker images)
        ImagesCommand.class
    }
)
public class DockerCommand {

}
