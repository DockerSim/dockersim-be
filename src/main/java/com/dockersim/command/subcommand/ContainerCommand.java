package com.dockersim.command.subcommand;


import com.dockersim.command.DockerCommand;
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
import com.dockersim.command.aliases.container.RenameCommand;
import com.dockersim.command.aliases.container.RestartCommand;
import com.dockersim.command.aliases.container.RunCommand;
import com.dockersim.command.aliases.container.StartCommand;
import com.dockersim.command.aliases.container.StopCommand;
import com.dockersim.command.aliases.container.UnpauseCommand;
import com.dockersim.command.subcommand.container.ContainerInspect;
import com.dockersim.command.subcommand.container.ContainerLsCommand;
import com.dockersim.command.subcommand.container.ContainerPruneCommand;
import com.dockersim.command.subcommand.container.ContainerRemoveCommand;
import com.dockersim.config.SimulationUserPrincipal;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Component
@Command(
    name = "container",
    description = "container command",
    subcommands = {
        // Aliases
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
//        PsCommand.class,
        RenameCommand.class,
        RestartCommand.class,
//        RmCommand.class,
        RunCommand.class,
        StartCommand.class,
        StopCommand.class,
        UnpauseCommand.class,

        // Subcommands
        ContainerInspect.class,
        ContainerLsCommand.class,
        ContainerPruneCommand.class,
        ContainerRemoveCommand.class
    }
)
public class ContainerCommand {

    @ParentCommand
    private DockerCommand parent;

    public SimulationUserPrincipal getPrincipal() {
        return parent.getPrincipal();
    }
}

