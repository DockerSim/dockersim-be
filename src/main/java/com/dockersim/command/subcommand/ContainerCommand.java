package com.dockersim.command.subcommand;

import com.dockersim.command.DockerCommand;
import com.dockersim.command.subcommand.container.*;
import com.dockersim.config.SimulationUserPrincipal;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "container",
        description = "container command",
        subcommands = {
                ContainerCreate.class,
                ContainerInspect.class,
                ContainerLs.class,
                ContainerPause.class,
                ContainerRestart.class,
                ContainerRm.class,
                ContainerStart.class,
                ContainerStop.class,
                ContainerUnpause.class,
        }
)
@RequiredArgsConstructor
public class ContainerCommand {

    @CommandLine.ParentCommand
    private DockerCommand parent;

    public SimulationUserPrincipal getPrincipal() {
        return parent.getPrincipal();
    }
}

