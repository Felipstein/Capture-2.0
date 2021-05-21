package br.felipe.capture.command.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.felipe.capture.Ranking;
import br.felipe.capture.command.Command;
import br.felipe.capture.config.ConfigManager;

public class WinsCommand extends Command {

	public WinsCommand() {
		super("wins");
	}

	@Override
	public boolean onCommand(boolean console, CommandSender s, Player p, String[] args) {
		if(!console) {
			String name = p.getName();
			boolean myself = args.length == 0 ? true : false;
			if(args.length > 0) {
				name = args[0];
				if(!ConfigManager.instance.hasProfile(name)) {
					if(p.isOp()) {
						if(args[0].equals("help")) {
							p.sendMessage("§6» §e/w set <jogador> <qnt> §fDefine a quantidade de wins de tal jogador.");
							return true;
						} else if(args[0].equals("set")) {
							if(args.length < 3) {
								return this.missingArguments(s, "/w set <jogador> <qnt>");
							}
							String target = args[1];
							if(!ConfigManager.instance.hasProfile(target)) {
								return this.profileDoesNotExists(s, target);
							}
							int value;
							try {
								value = Integer.valueOf(args[2]);
							} catch(NumberFormatException e) {
								return this.invalidNumberValue(s, args[2]);
							}
							ConfigManager.instance.setWins(target, value);
							p.sendMessage("§aDefinido a quantidade de vitórias de §6" + target + " §apara §e" + value + "§a.");
							return true;
						} else {
							return this.invalidArgument(s, args[0]);
						}
					} else {
						return this.profileDoesNotExists(s, name);
					}
				}
				if(name.equalsIgnoreCase(p.getName())) {
					myself = true;
				}
			}
			p.sendMessage("§6[§fWins§6] §6Vitórias" + (myself ? "" : " de " + name) + ": §f" + ConfigManager.instance.getWins(name) + " win" + (ConfigManager.instance.getWins(name) > 1 ? "s" : "") + ".");
			p.sendMessage("§6[§eRanking§6] Posição" + (myself ? "" : " de " + name) + ": §e" + Ranking.getPositionOf(name) + " lugar.");
			p.sendMessage("§7§oVeja o ranking com /ranking!");
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(Player p, String[] args) {
		return null;
	}
	
}