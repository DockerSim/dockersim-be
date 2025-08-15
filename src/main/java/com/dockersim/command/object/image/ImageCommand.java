package com.dockersim.command.object.image;


import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "image",
        description = "docker image",
        subcommands = {
                ImagePullCommand.class,
//                ImageImagesCommand.class,
//                ImageRmiCommand.class,
//                ImageBuildCommand.class,
//                ImageLsCommand.class,
//                ImagePruneCommand.class,
//                ImageInspectCommand.class,
//                ImageHistoryCommand.class
        }
)
public class ImageCommand { }
