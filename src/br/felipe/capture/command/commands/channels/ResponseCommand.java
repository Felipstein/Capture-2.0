package br.felipe.capture.command.commands.channels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.felipe.capture.channels.Channel;

public class ResponseCommand extends ChannelCommand {

	public ResponseCommand() {
		super("response");
	}
	
	@Override
	public boolean onCommand(boolean console, CommandSender s, Player p, String[] args) {
		if(args.length == 0) {
			return this.missingArguments(s, "/r <mensagem>");
		}
		if(!Channel.response.containsKey(s)) {
			s.sendMessage("§cVocê não tem ninguém para responder.");
			return false;
		}
		Channel.sendPrivateMessage(s, Channel.response.get(s), buildMessage(args, 1));
		return true;
	}

	@Override
	public List<String> onTabComplete(Player p, String[] args) {
		if(args.length == 1) {
			if(args[0].isEmpty()) {
				if(Channel.response.containsKey(p)) {
					return new ArrayList<>(Arrays.asList("<mensagem para " + (Channel.response.get(p) instanceof Player ? Channel.response.get(p).getName() : "CONSOLE") + ">"));
				} else {
					return new ArrayList<>(Arrays.asList("Você não tem ninguém para responder"));
				}
			}
		}
		return null;
	}
	
}