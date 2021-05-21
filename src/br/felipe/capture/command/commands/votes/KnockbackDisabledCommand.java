package br.felipe.capture.command.commands.votes;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.felipe.capture.command.Command;
import br.felipe.capture.game.Match;
import br.felipe.capture.game.MatchsManager;

public class KnockbackDisabledCommand extends Command {
	
	public KnockbackDisabledCommand() {
		super("knockbackdesativado");
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
		if(match.getKnockbackDisabledVoteds().contains(p)) {
			p.sendMessage("§cVocê já votou para KNOCKBACK DESATIVADO.");
			return false;
		}
		if(match.getKnockbackEnabledVoteds().contains(p)) {
			match.getVotes().addKnockbackEnabledVotes(-1);
			match.getKnockbackEnabledVoteds().remove(p);
		}
		match.getVotes().addKnockbackDisabledVotes(1);
		match.getKnockbackDisabledVoteds().add(p);
		match.updateVotes();
		p.sendMessage("§aVocê votou para KNOCKBACK DESATIVADO!");
		return true;
	}

	@Override
	public List<String> onTabComplete(Player p, String[] args) {
		return null;
	}
	
}