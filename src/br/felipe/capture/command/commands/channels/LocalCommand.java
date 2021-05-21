package br.felipe.capture.command.commands.channels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.felipe.capture.channels.Channel;

public class LocalCommand extends ChannelCommand {

	public LocalCommand() {
		super("local");
	}

	@Override
	public boolean onCommand(boolean console, CommandSender s, Player p, String[] args) {
		if(console) {
			return this.playerOnly(s);
		}
		if(args.length == 0) {
			s.sendMessage("§cRestam argumentos! Tente \"/l <mensagem>\" ou \"<mensagem>\".");
			return false;
		}
		Channel.sendLocalMessage(p, buildMessage(args, 1));
		return false;
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