package com.dockersim.command.subcommand;


import com.dockersim.command.DockerCommand;
import com.dockersim.command.subcommand.image.ImageBuild;
import com.dockersim.command.subcommand.image.ImageHistory;
import com.dockersim.command.subcommand.image.ImageInspect;
import com.dockersim.command.subcommand.image.ImageLs;
import com.dockersim.command.subcommand.image.ImagePrune;
import com.dockersim.command.subcommand.image.ImagePull;
import com.dockersim.command.subcommand.image.ImagePush;
import com.dockersim.command.subcommand.image.ImageRm;
import com.dockersim.config.SimulationUserPrincipal;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
	name = "image",
	description = "image command",
	subcommands = {
		ImageBuild.class,
		ImageHistory.class,
		ImageInspect.class,
		ImageLs.class,
		ImagePrune.class,
		ImagePull.class,
		ImagePush.class,
		ImageRm.class
	}
)
@RequiredArgsConstructor
public class ImageCommand {

	@ParentCommand
	private DockerCommand parent;

	public SimulationUserPrincipal getPrincipal() {
		return parent.getPrincipal();
	}
}
