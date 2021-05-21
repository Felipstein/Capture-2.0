package br.felipe.capture.command.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.felipe.capture.command.Command;
import br.felipe.capture.config.ConfigManager;

public class ProfileCommand extends Command {

	public ProfileCommand() {
		super("profile");
	}
	
	@Override
	public boolean onCommand(boolean console, CommandSender s, Player p, String[] args) {
		if(console) {
			return this.playerOnly(s);
		}
		String name = p.getName();
		if(!ConfigManager.instance.hasProfile(name)) {
			p.sendMessage("§cVocê não tem perfil.");
			return false;
		}
		int matchs = ConfigManager.instance.getMatchs(name);
		int wins = ConfigManager.instance.getWins(name);
		p.sendMessage("§6Partidas jogadas: §f" + matchs);
		p.sendMessage("§6Vitórias: §f" + wins);
		p.sendMessage("§6Derrotas: §c" + (wins - matchs));
		p.sendMessage("§6Total capturados: §f" + ConfigManager.instance.getCaptured(name));
		p.sendMessage("§6Total salvados: §f" + ConfigManager.instance.getSaved(name));
		p.sendMessage("§6Total foi capturado: §f" + ConfigManager.instance.getBeenCaptured(name));
		return true;
	}
	
	@Override
	public List<String> onTabComplete(Player p, String[] args) {
		return null;
	}
	
}