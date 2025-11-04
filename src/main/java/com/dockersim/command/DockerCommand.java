package com.dockersim.command;


import com.dockersim.command.aliases.container.*;
import com.dockersim.command.aliases.image.*;
import com.dockersim.command.subcommand.ContainerCommand;
import com.dockersim.command.subcommand.ImageCommand;
import com.dockersim.command.subcommand.NetworkCommand;
import com.dockersim.command.subcommand.VolumeCommand;
import com.dockersim.config.SimulationUserPrincipal;
import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;

@CommandLine.Command(name = "docker",
        subcommands = {
                // Docker Object
                ImageCommand.class,
                ContainerCommand.class,
                VolumeCommand.class,
                NetworkCommand.class,

                // Image Aliases
                Build.class,
                History.class,
                Images.class,
                Pull.class,
                Push.class,
                Rmi.class,

                // Container Aliases
                Create.class,
                Ps.class,
                Pause.class,
                Restart.class,
                Rm.class,
                Start.class,
                Stop.class,
                Unpause.class

                // Volume Aliases
                // none

                // Network Aliases
        }
)
@Getter
@Setter
public class DockerCommand {
    private SimulationUserPrincipal principal;
}
