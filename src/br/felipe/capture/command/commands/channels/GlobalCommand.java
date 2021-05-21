package br.felipe.capture.command.commands.channels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.felipe.capture.channels.Channel;

public class GlobalCommand extends ChannelCommand {
	
	public GlobalCommand() {
		super("global");
	}
	
	@Override
	public boolean onCommand(boolean console, CommandSender s, Player p, String[] args) {
		if(args.length == 0) {
			return this.missingArguments(s, "/g <mensagem>");
		}
		Channel.sendGlobalMessage(s, this.buildMessage(args, 1));
		return true;
	}

	@Override
	public List<String> onTabComplete(Player p, String[] args) {
		if(args.length == 1) {
			if(args[0].isEmpty()) {
				return new ArrayList<>(Arrays.asList("<mensagem>"));
			}
		}
		return null;
	}
	
}