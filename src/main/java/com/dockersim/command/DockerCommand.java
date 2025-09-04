package com.dockersim.command;

import com.dockersim.command.aliases.container.AttachCommand;
import com.dockersim.command.aliases.container.CommitCommand;
import com.dockersim.command.aliases.container.CpCommand;
import com.dockersim.command.aliases.container.CreateCommand;
import com.dockersim.command.aliases.container.DiffCommand;
import com.dockersim.command.aliases.container.ExecCommand;
import com.dockersim.command.aliases.container.ExportCommand;
import com.dockersim.command.aliases.container.KillCommand;
import com.dockersim.command.aliases.container.PauseCommand;
import com.dockersim.command.aliases.container.PortCommand;
import com.dockersim.command.aliases.container.PsCommand;
import com.dockersim.command.aliases.container.RenameCommand;
import com.dockersim.command.aliases.container.RestartCommand;
import com.dockersim.command.aliases.container.RmCommand;
import com.dockersim.command.aliases.container.RunCommand;
import com.dockersim.command.aliases.container.StartCommand;
import com.dockersim.command.aliases.container.StopCommand;
import com.dockersim.command.aliases.container.UnpauseCommand;
import com.dockersim.command.aliases.image.ImagesCommand;
import com.dockersim.command.subcommand.ContainerCommand;
import com.dockersim.command.subcommand.ImageCommand;
import com.dockersim.command.subcommand.VolumeCommand;
import com.dockersim.config.SimulationUserPrincipal;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "docker",
    subcommands = {
        // Docker Object
        ImageCommand.class,
        ContainerCommand.class,
        VolumeCommand.class,

        // Image Aliases
        ImagesCommand.class,

        // Container Aliases
        AttachCommand.class,
        CommitCommand.class,
        CpCommand.class,
        CreateCommand.class,
        DiffCommand.class,
        ExecCommand.class,
        ExportCommand.class,
        KillCommand.class,
        PauseCommand.class,
        PortCommand.class,
        PsCommand.class,
        RenameCommand.class,
        RestartCommand.class,
        RmCommand.class,
        RunCommand.class,
        StartCommand.class,
        StopCommand.class,
        UnpauseCommand.class
    }
)

@Getter
@Setter
public class DockerCommand {

    private SimulationUserPrincipal principal;
}
