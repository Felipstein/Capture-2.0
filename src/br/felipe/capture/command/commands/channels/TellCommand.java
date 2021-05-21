package br.felipe.capture.command.commands.channels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.felipe.capture.channels.Channel;
import br.felipe.capture.utils.ListUtils;
import br.felipe.capture.utils.PlayerUtils;

public class TellCommand extends ChannelCommand {

	public TellCommand() {
		super("tell");
	}

	@Override
	public boolean onCommand(boolean console, CommandSender s, Player p, String[] args) {
		if(args.length == 0) {
			return this.missingArguments(s, "/tell <jogador> <mensagem>");
		}
		if(args.length == 1) {
			return this.missingArguments(s, "/tell " + args[0] + " <mensagem>");
		}
		if(args[0].equalsIgnoreCase("CONSOLE")) {
			Channel.sendPrivateMessage(s, Bukkit.getServer().getConsoleSender(), buildMessage(args, 2));
		} else {
			Player target = Bukkit.getPlayer(args[0]);
			if(target == null) {
				return this.playerOffline(s, args[0]);
			}
			Channel.sendPrivateMessage(s, target, buildMessage(args, 2));
		}
		return true;
	}
	
	@Override
	public List<String> onTabComplete(Player p, String[] args) {
		if(args.length == 1) {
			return ListUtils.getElementsStartingWith(args[0], true, PlayerUtils.getOnlinePlayersName());
		}
		if(args.length == 2) {
			if(args[1].isEmpty()) {
				return new ArrayList<>(Arrays.asList("<mensagem para " + args[0] + ">"));
			}
		}
		return null;
	}
	
}