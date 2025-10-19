package com.dockersim.command.subcommand;

import org.springframework.stereotype.Component;

import com.dockersim.command.DockerCommand;
import com.dockersim.command.subcommand.network.NetworkConnectCommand;
import com.dockersim.command.subcommand.network.NetworkCreateCommand;
import com.dockersim.command.subcommand.network.NetworkDisconnectCommand;
import com.dockersim.command.subcommand.network.NetworkInspectCommand;
import com.dockersim.command.subcommand.network.NetworkLsCommand;
import com.dockersim.command.subcommand.network.NetworkPruneCommand;
import com.dockersim.command.subcommand.network.NetworkRmCommand;
import com.dockersim.config.SimulationUserPrincipal;

import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Component
@Command(
	name = "network",
	description = "network command",
	subcommands = {
		NetworkConnectCommand.class,
		NetworkCreateCommand.class,
		NetworkDisconnectCommand.class,
		NetworkInspectCommand.class,
		NetworkLsCommand.class,
		NetworkPruneCommand.class,
		NetworkRmCommand.class
	})
public class NetworkCommand {

	@ParentCommand
	private DockerCommand parent;

	public SimulationUserPrincipal getPrincipal() {
		return parent.getPrincipal();
	}
}