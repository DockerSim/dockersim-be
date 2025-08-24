package com.dockersim.command;

import com.dockersim.command.aliases.image.ImagesCommand;
import com.dockersim.command.subcommand.ImageCommand;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "docker", mixinStandardHelpOptions = true,
    subcommands = {
        ImageCommand.class,
        ImagesCommand.class
    }
)
public class DockerCommand {

}
