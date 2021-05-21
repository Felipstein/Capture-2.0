package br.felipe.capture.command.commands.channels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.felipe.capture.channels.Channel;
import br.felipe.capture.game.Match;
import br.felipe.capture.game.MatchsManager;

public class TeamCommand extends ChannelCommand {

	public TeamCommand() {
		super("team");
	}

	@Override
	public boolean onCommand(boolean console, CommandSender s, Player p, String[] args) {
		if(console) {
			return this.playerOnly(s);
		}
		if(args.length == 0) {
			return this.missingArguments(s, "/e <mensagem>");
		}
		if(!MatchsManager.instance.playerIsInMatch(p)) {
			p.sendMessage("§cVocê não está em uma partida.");
			return false;
		}
		Match match = MatchsManager.instance.getMatchOfPlayer(p);
		if(match.waitingOrStarting()) {
			p.sendMessage("§cA sua equipe ainda não foi sorteada.");
			return false;
		}
		Channel.sendTeamMessage(match.getCasted(p), buildMessage(args, 1));
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