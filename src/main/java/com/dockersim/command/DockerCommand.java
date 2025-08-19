package com.dockersim.command;


import com.dockersim.command.object.image.ImageCommand;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "docker", mixinStandardHelpOptions = true,
    subcommands = {
        // 1. 객체 명령어 (계층 구조)
//            ContainerCommand.class,
        ImageCommand.class,
//            VolumeCommand.class,
//            NetworkCommand.class,

        // 2. 단축(레거시) 명령어
//            RunCommand.class, CreateCommand.class, StartCommand.class, RestartCommand.class, DiffCommand.class,
//            PsCommand.class, StopCommand.class, RmCommand.class, LogsCommand.class, ExecCommand.class, CpCommand.class,
//            PullCommand.class, ImagesCommand.class, RmiCommand.class, BuildCommand.class, HistoryCommand.class,
//            TagCommand.class,
//            LoginCommand.class,
//            VersionCommand.class

    }
)
public class DockerCommand {

}
