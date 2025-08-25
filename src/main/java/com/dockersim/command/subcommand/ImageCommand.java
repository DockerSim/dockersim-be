package com.dockersim.command.subcommand;

import com.dockersim.command.subcommand.image.ImageBuildCommand;
import com.dockersim.command.subcommand.image.ImageInspectCommand;
import com.dockersim.command.subcommand.image.ImageLsCommand;
import com.dockersim.command.subcommand.image.ImagePruneCommand;
import com.dockersim.command.subcommand.image.ImagePullCommand;
import com.dockersim.command.subcommand.image.ImagePushCommand;
import com.dockersim.command.subcommand.image.ImageRemoveCommand;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "image",
    description = "image command",
    subcommands = {
        ImagePullCommand.class,
        ImagePushCommand.class, // push 명령어 등록
        ImageRemoveCommand.class,
        ImageBuildCommand.class,
        ImagePruneCommand.class,
        ImageInspectCommand.class,
        ImageLsCommand.class
    }
)
public class ImageCommand {

}
