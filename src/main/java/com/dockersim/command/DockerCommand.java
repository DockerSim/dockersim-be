package com.dockersim.command;

import org.springframework.stereotype.Component;

import com.dockersim.command.aliases.container.Attach;
import com.dockersim.command.aliases.container.Commit;
import com.dockersim.command.aliases.container.Cp;
import com.dockersim.command.aliases.container.Create;
import com.dockersim.command.aliases.container.Exec;
import com.dockersim.command.aliases.container.Kill;
import com.dockersim.command.aliases.container.Pause;
import com.dockersim.command.aliases.container.Port;
import com.dockersim.command.aliases.container.Ps;
import com.dockersim.command.aliases.container.Rename;
import com.dockersim.command.aliases.container.Restart;
import com.dockersim.command.aliases.container.Rm;
import com.dockersim.command.aliases.container.Run;
import com.dockersim.command.aliases.container.Start;
import com.dockersim.command.aliases.container.Stop;
import com.dockersim.command.aliases.container.Unpause;
import com.dockersim.command.aliases.image.Build;
import com.dockersim.command.aliases.image.History;
import com.dockersim.command.aliases.image.Images;
import com.dockersim.command.aliases.image.Pull;
import com.dockersim.command.aliases.image.Push;
import com.dockersim.command.aliases.image.Rmi;
import com.dockersim.command.subcommand.ContainerCommand;
import com.dockersim.command.subcommand.ImageCommand;
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

		// Image Aliases
		Build.class,
		History.class,
		Images.class,
		Pull.class,
		Push.class,
		Rmi.class,

		// Container Aliases
		Attach.class,
		Commit.class,
		Cp.class,
		Create.class,
		Exec.class,
		Kill.class,
		Ps.class,
		Pause.class,
		Port.class,
		Rename.class,
		Restart.class,
		Rm.class,
		Run.class,
		Start.class,
		Stop.class,
		Unpause.class

		// Volume Aliases
		// none

		// Network Aliases
	}
)
@Component
@Getter
@Setter
public class DockerCommand {
	private SimulationUserPrincipal principal;
}
