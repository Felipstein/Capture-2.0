package br.felipe.capture.command.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.felipe.capture.command.Command;
import br.felipe.capture.game.MatchsManager;
import br.felipe.capture.shop.ShopInventory;

public class ShopCommand extends Command {

	public ShopCommand() {
		super("shop");
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
		if(!MatchsManager.instance.getMatchOfPlayer(p).getSettings().containsShop()) {
			p.sendMessage("§cA loja não está liberada nessa partida.");
			return false;
		}
		ShopInventory.openInventory(p);
		return true;
	}

	@Override
	public List<String> onTabComplete(Player p, String[] args) {
		return null;
	}
	
}