package br.felipe.capture.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import br.felipe.capture.Main;

public class CommandsManager {
	
	private static Set<Command> commands = new HashSet<>();
	
	public static void putCommand(Command command) {
		Main.getPlugin().getCommand(command.getSyntax()).setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, org.bukkit.command.Command cmd, String label, String[] args) {
				boolean console = !(s instanceof Player);
				Player p = null;
				if(!console) {
					p = (Player) s;
					if(command.requiresOp() && !p.isOp()) {
						p.sendMessage("§cSem permissão para isso!");
						return false;
					}
				}
				return command.onCommand(console, s, p, args);
			}
		});
		Main.getPlugin().getCommand(command.getSyntax()).setTabCompleter(new TabCompleter() {
			@Override
			public List<String> onTabComplete(CommandSender s, org.bukkit.command.Command cmd, String label, String[] args) {
				if(!(s instanceof Player)) {
					return null;
				}
				Player p = (Player) s;
				if(command.requiresOp()) {
					if(!p.isOp()) {
						return new ArrayList<String>();
					}
				}
				List<String> list = command.onTabComplete(p, args);
				if(list == null) {
					return new ArrayList<String>();
				}
				return list;
			}
		});
		commands.add(command);
	}
	
}