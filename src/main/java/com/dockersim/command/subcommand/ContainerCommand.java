package com.dockersim.command.subcommand;

import org.springframework.stereotype.Component;

import com.dockersim.command.DockerCommand;
import com.dockersim.command.subcommand.container.ContainerAttach;
import com.dockersim.command.subcommand.container.ContainerCommit;
import com.dockersim.command.subcommand.container.ContainerCp;
import com.dockersim.command.subcommand.container.ContainerCreate;
import com.dockersim.command.subcommand.container.ContainerExec;
import com.dockersim.command.subcommand.container.ContainerInspect;
import com.dockersim.command.subcommand.container.ContainerKill;
import com.dockersim.command.subcommand.container.ContainerLs;
import com.dockersim.command.subcommand.container.ContainerPause;
import com.dockersim.command.subcommand.container.ContainerPort;
import com.dockersim.command.subcommand.container.ContainerPrune;
import com.dockersim.command.subcommand.container.ContainerRename;
import com.dockersim.command.subcommand.container.ContainerRestart;
import com.dockersim.command.subcommand.container.ContainerRm;
import com.dockersim.command.subcommand.container.ContainerRun;
import com.dockersim.command.subcommand.container.ContainerStart;
import com.dockersim.command.subcommand.container.ContainerStop;
import com.dockersim.command.subcommand.container.ContainerUnpause;
import com.dockersim.config.SimulationUserPrincipal;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@CommandLine.Command(name = "container",
	description = "container command",
	subcommands = {
		ContainerAttach.class,
		ContainerCommit.class,
		ContainerCp.class,
		ContainerCreate.class,
		ContainerExec.class,
		ContainerInspect.class,
		ContainerKill.class,
		ContainerLs.class,
		ContainerPause.class,
		ContainerPort.class,
		ContainerPrune.class,
		ContainerRename.class,
		ContainerRestart.class,
		ContainerRm.class,
		ContainerRun.class,
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

