package br.felipe.capture.command;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class Command {
	
	private String syntax;
	
	private boolean requiresOp;
	
	public Command(String syntax) {
		this(syntax, false);
	}
	
	public Command(String syntax, boolean requiresOp) {
		this.syntax = syntax;
		this.requiresOp = requiresOp;
	}
	
	public String getSyntax() {
		return syntax;
	}
	
	public boolean requiresOp() {
		return requiresOp;
	}
	
	public boolean invalidArgument(CommandSender s, String invalid) {
		s.sendMessage("§c\"" + invalid + "\" não é um argumento valido.");
		return false;
	}
	
	public boolean missingArguments(CommandSender s) {
		s.sendMessage("§cRestam argumentos! Escreva o comando novamente e analise as opções oferecidas.");
		return false;
	}
	
	public boolean missingArguments(CommandSender s, String type) {
		if(!type.startsWith("/")) {
			type = "/" + type;
		}
		s.sendMessage("§cRestam argumentos! Tente \"" + type + "\".");
		return false;
	}
	
	public boolean invalidNumberValue(CommandSender s, String invalid) {
		s.sendMessage("§c\"" + invalid + "\" não representa como um valor númerico.");
		return false;
	}
	
	public boolean playerOffline(CommandSender s, String name) {
		s.sendMessage("§cO jogador \"" + name + "\" está off-line.");
		return false;
	}
	
	public boolean playerOnly(CommandSender s) {
		s.sendMessage("§cComando indisponível para o CONSOLE");
		return false;
	}
	
	public boolean consoleOnly(Player p) {
		p.sendMessage("§cComando disponível apenas para o CONSOLE");
		return false;
	}
	
	public boolean profileDoesNotExists(CommandSender s, String profileName) {
		s.sendMessage("§cO perfil do jogador \"" + profileName + "\" não existe.");
		return false;
	}
	
	public abstract boolean onCommand(boolean console, CommandSender s, Player p, String[] args);
	
	public abstract List<String> onTabComplete(Player p, String[] args);
	
}