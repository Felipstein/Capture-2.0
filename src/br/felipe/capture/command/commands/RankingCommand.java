package br.felipe.capture.command.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.felipe.capture.Ranking;
import br.felipe.capture.command.Command;
import br.felipe.capture.config.ConfigManager;

public class RankingCommand extends Command {
	
	public RankingCommand() {
		super("ranking");
	}

	@Override
	public boolean onCommand(boolean console, CommandSender s, Player p, String[] args) {
		if(console) {
			return false;
		}
		if(!Ranking.getAllWins().isEmpty()) {
			p.sendMessage("§6§l§m----------------------------------------------------");
			int myselfPosition = Ranking.getPositionOf(p.getName());
			int position = 1;
			for(String name : Ranking.getRanking()) {
				if(position > 10) {
					break;
				}
				String color = "§c";
				if	   (position == 1) color = "§e";
				else if(position == 2) color = "§7";
				else if(position == 3) color = "§6";
				s.sendMessage(color + position + ". §f" + (position == myselfPosition ? "§l" : "") + name + " " + color + "- §f" + (position == myselfPosition ? "§l" : "") + ConfigManager.instance.getWins(name) + " win" + (ConfigManager.instance.getWins(name) > 1 ? "s" : ""));
				++position;
			}
			if(myselfPosition > 10) {
				s.sendMessage("§c...");
				s.sendMessage("§c" + myselfPosition + ". §f§l" + p.getName() + " §c- §f§l" + ConfigManager.instance.getWins(p.getName()) + " win" + (ConfigManager.instance.getWins(p.getName()) > 1 ? "s" : ""));
			}
			return true;
		}
		s.sendMessage("§cAinda não foi realizada nenhuma partida com êxito.");
		return false;
	}

	@Override
	public List<String> onTabComplete(Player p, String[] args) {
		return null;
	}
	
	
	
}