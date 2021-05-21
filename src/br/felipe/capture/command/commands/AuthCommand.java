package br.felipe.capture.command.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.felipe.capture.OpUnavailable;
import br.felipe.capture.command.Command;

public class AuthCommand extends Command {

	public AuthCommand() {
		super("auth");
	}

	@Override
	public boolean onCommand(boolean console, CommandSender s, Player p, String[] args) {
		return OpUnavailable.available(p);
	}

	@Override
	public List<String> onTabComplete(Player p, String[] args) {
		return null;
	}
	
}