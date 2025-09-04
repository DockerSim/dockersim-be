package com.dockersim.command.subcommand;

import com.dockersim.command.DockerCommand;
import com.dockersim.command.subcommand.volume.VolumeCreateCommand;
import com.dockersim.command.subcommand.volume.VolumeInspectCommand;
import com.dockersim.command.subcommand.volume.VolumeLsCommand;
import com.dockersim.command.subcommand.volume.VolumePruneCommand;
import com.dockersim.command.subcommand.volume.VolumeRmCommand;
import com.dockersim.config.SimulationUserPrincipal;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;


@Component
@Command(
    name = "volume",
    description = "volume command",
    subcommands = {
        VolumeCreateCommand.class,
        VolumeInspectCommand.class,
        VolumeLsCommand.class,
        VolumePruneCommand.class,
        VolumeRmCommand.class
    })
public class VolumeCommand {

    @ParentCommand
    private DockerCommand parent;

    public SimulationUserPrincipal getPrincipal() {
        return parent.getPrincipal();
    }
}