package com.dockersim.command.subcommand;

import com.dockersim.command.DockerCommand;
import com.dockersim.command.subcommand.image.ImageBuildCommand;
import com.dockersim.command.subcommand.image.ImageHistoryCommand;
import com.dockersim.command.subcommand.image.ImageInspectCommand;
import com.dockersim.command.subcommand.image.ImageLsCommand;
import com.dockersim.command.subcommand.image.ImagePruneCommand;
import com.dockersim.command.subcommand.image.ImagePullCommand;
import com.dockersim.command.subcommand.image.ImagePushCommand;
import com.dockersim.command.subcommand.image.ImageRmCommand;
import com.dockersim.config.SimulationUserPrincipal;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Component
@Command(
    name = "image",
    description = "image command",
    subcommands = {
        ImageBuildCommand.class,
        ImageHistoryCommand.class,
        ImageInspectCommand.class,
        ImageLsCommand.class,
        ImagePruneCommand.class,
        ImagePullCommand.class,
        ImagePushCommand.class,
        ImageRmCommand.class
    }
)

public class ImageCommand {

    @ParentCommand
    private DockerCommand parent;

    public SimulationUserPrincipal getPrincipal() {
        return parent.getPrincipal();
    }
}
