package br.felipe.capture.command.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.felipe.capture.Balance;
import br.felipe.capture.command.Command;
import br.felipe.capture.config.ConfigManager;
import br.felipe.capture.utils.Formats;
import br.felipe.capture.utils.ListUtils;
import br.felipe.capture.utils.PlayerUtils;

public class BalanceCommand extends Command {

	public BalanceCommand() {
		super("balance");
	}

	@Override
	public boolean onCommand(boolean console, CommandSender s, Player p, String[] args) {
		if(!console) {
			if(p.isOp()) {
				if(args.length == 0) {
					p.sendMessage("§2[§fMoney§2] Balanço: §f$" + Formats.decimalFormat(ConfigManager.instance.getBalance(p.getName())) + " coin" + (ConfigManager.instance.getBalance(p.getName()) > 1 ? "s" : "") + ".");
					return true;
				}
				if(args[0].equals("help")) {
					p.sendMessage("§6§ §a/m total §fContate o total de money que existe no servidor.");
					p.sendMessage("§6§ §a/m top §fContate o ranking de money do servidor.");
					p.sendMessage("§6§ §a/m set <jogador> <qnt> §fDefine a quantia de money para tal jogador.");
					p.sendMessage("§6§ §a/m deposit <jogador> <qnt> §fAdicione uma quantia de money para tal jogador.");
					p.sendMessage("§6§ §a/m withdraw <jogador> <qnt> §fRetire uma quantia de money de tal jogador.");
					p.sendMessage("§6§ §a/m reset <jogador> §fResete o balanço de tal jogador.");
					return true;
				} else if(args[0].equals("total")) {
					p.sendMessage("§2[§fMoney§2] Total somado: §f$" + Formats.decimalFormat(Balance.getTotal()) + " coins.");
					return true;
				} else if(args[0].equals("top")) {
					int position = 1;
					for(String name : Balance.getRanking()) {
						p.sendMessage("§2" + position + ". §f" + name + " §a- §f" + Formats.decimalFormat(ConfigManager.instance.getBalance(name)) + " coin" + (ConfigManager.instance.getBalance(name) > 1 ? "s" : "") + ".");
						++position;
					}
					return true;
				} else if(args[0].equals("set")) {
					if(args.length < 3) {
						return this.missingArguments(s, "m set <jogador> <qnt>");
					}
					String name = args[1];
					if(!ConfigManager.instance.hasProfile(name)) {
						p.sendMessage("§cEsse jogador não existe.");
						return false;
					}
					double value;
					try {
						value = Double.valueOf(args[2]);
					} catch(NumberFormatException e) {
						return this.invalidNumberValue(s, args[2]);
					}
					p.sendMessage("§aBalanço de §6" + name + " §adefinido para §e$" + Formats.decimalFormat(value) + "§a.");
					ConfigManager.instance.setBalance(name, value);
					return true;
				} else if(args[0].equals("deposit")) {
					if(args.length < 3) {
						return this.missingArguments(s, "m deposit <jogador> <qnt>");
					}
					String name = args[1];
					if(!ConfigManager.instance.hasProfile(name)) {
						p.sendMessage("§cEsse jogador não existe.");
						return false;
					}
					double value;
					try {
						value = Double.valueOf(args[2]);
					} catch(NumberFormatException e) {
						return this.invalidNumberValue(s, args[2]);
					}
					p.sendMessage("§aDepositado §e$" + Formats.decimalFormat(value) + " §apara §6" + name + "§a.");
					ConfigManager.instance.depositBalance(name, value);
					return true;
				} else if(args[0].equals("withdraw")) {
					if(args.length < 3) {
						return this.missingArguments(s, "m withdraw <jogador> <qnt>");
					}
					String name = args[1];
					if(!ConfigManager.instance.hasProfile(name)) {
						p.sendMessage("§cEsse jogador não existe.");
						return false;
					}
					double value;
					try {
						value = Double.valueOf(args[2]);
					} catch(NumberFormatException e) {
						return this.invalidNumberValue(s, args[2]);
					}
					p.sendMessage("§aRetirado §e$" + Formats.decimalFormat(value) + " §ade §6" + name + "§a.");
					ConfigManager.instance.withdrawBalance(name, value);
					return true;
				} else if(args[0].equals("reset")) {
					if(args.length < 2) {
						return this.missingArguments(s, "m reset <jogador>");
					}
					String name = args[1];
					if(!ConfigManager.instance.hasProfile(name)) {
						p.sendMessage("§cEsse jogador não existe.");
						return false;
					}
					p.sendMessage("§aBalanço de §6" + name + " §aresetado.");
					ConfigManager.instance.setBalance(name, 0.0d);
					return true;
				} else {
					String name = args[0];
					if(!ConfigManager.instance.hasProfile(name)) {
						p.sendMessage("§cEsse jogador não existe.");
						return false;
					}
					p.sendMessage("§2[§fMoney§2] §2Balanço de " + name + ": §f" + Formats.decimalFormat(ConfigManager.instance.getBalance(name)) + " coin" + (ConfigManager.instance.getBalance(name) > 1 ? "s" : "") + ".");
					return true;
				}
			} else {
				p.sendMessage("§2[§fMoney§2] Balanço: §f$" + Formats.decimalFormat(ConfigManager.instance.getBalance(p.getName())) + " coins.");
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(Player p, String[] args) {
		if(args.length == 1) {
			if(args[0].isEmpty()) {
				return ListUtils.getElementsStartingWith(args[0], "<jogador>", "help", "total", "top", "set", "deposit", "withdraw", "reset");
			} else {
				return ListUtils.getElementsStartingWith(args[0], "help", "total", "top", "set", "deposit", "withdraw", "reset");
			}
		}
		if(args[0].equals("set") || args[0].equals("deposit") || args[0].equals("withdraw")) {
			if(args.length == 2) {
				return ListUtils.getElementsStartingWith(args[1], true, PlayerUtils.getOnlinePlayersName());
			}
			if(args.length == 3) {
				if(args[2].isEmpty()) {
					return new ArrayList<>(Arrays.asList("<quantidade>"));
				}
			}
		}
		if(args[0].equals("reset")) {
			if(args.length == 2) {
				return ListUtils.getElementsStartingWith(args[1], true, PlayerUtils.getOnlinePlayersName());
			}
		}
		return null;
	}
	
}