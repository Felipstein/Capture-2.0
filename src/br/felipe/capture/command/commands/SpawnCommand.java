package br.felipe.capture.command.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.felipe.capture.command.Command;
import br.felipe.capture.config.ConfigManager;
import br.felipe.capture.game.MatchsManager;

public class SpawnCommand extends Command {

	public SpawnCommand() {
		super("spawn");
	}
	
	@Override
	public boolean onCommand(boolean console, CommandSender s, Player p, String[] args) {
		if(console) {
			return false;
		}
		if(MatchsManager.instance.playerIsInMatch(p)) {
			MatchsManager.instance.disconnectPlayerOfMatch(p);
			p.sendMessage("§cVocê desconectou da partida.");
		} else {
			p.teleport(ConfigManager.instance.getLobbyLocation());
		}
		return true;
	}
	
	@Override
	public List<String> onTabComplete(Player p, String[] args) {
		return null;
	}
	
}