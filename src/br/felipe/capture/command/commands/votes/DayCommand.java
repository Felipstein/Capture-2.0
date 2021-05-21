package br.felipe.capture.command.commands.votes;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.felipe.capture.command.Command;
import br.felipe.capture.game.Match;
import br.felipe.capture.game.MatchsManager;

public class DayCommand extends Command {
	
	public DayCommand() {
		super("dia");
	}

	@Override
	public boolean onCommand(boolean console, CommandSender s, Player p, String[] args) {
		if(console) {
			return this.playerOnly(s);
		}
		if(!MatchsManager.instance.playerIsInMatch(p)) {
			p.sendMessage("§cVocê não está em nenhuma partida.");
			return false;
		}
		Match match = MatchsManager.instance.getMatchOfPlayer(p);
		if(!match.waitingOrStarting()) {
			p.sendMessage("§cSua partida já começou.");
			return false;
		}
		if(match.getTimeDayVoteds().contains(p)) {
			p.sendMessage("§cVocê já votou para DIA.");
			return false;
		}
		if(match.getTimeNightVoteds().contains(p)) {
			match.getVotes().addNightVotes(-1);
			match.getTimeNightVoteds().remove(p);
		}
		match.getVotes().addDayVotes(1);
		match.getTimeDayVoteds().add(p);
		match.updateVotes();
		p.sendMessage("§aVocê votou para DIA!");
		return true;
	}

	@Override
	public List<String> onTabComplete(Player p, String[] args) {
		return null;
	}
	
}