package br.felipe.capture.channels;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Predicate;

import br.felipe.capture.Main;
import br.felipe.capture.config.ConfigManager;
import br.felipe.capture.game.MatchsManager;
import br.felipe.capture.game.profiles.Capturer;
import br.felipe.capture.game.profiles.Team;

public class Channel {
	
	public static Map<CommandSender, CommandSender> response = new HashMap<>();
	
	public static void sendPrivateMessage(CommandSender sender, CommandSender receiver, Object message) {
		sender.sendMessage("§8(Mensagem para " + getName(receiver) + "): §2" + message);
		receiver.sendMessage("§8(Mensagem de " + getName(sender) + "): §2" + message);
		response.put(sender, receiver);
		response.put(receiver, sender);
		Main.getPlugin().getLogger().info("[PRIVADO] [destinatario " + getName(receiver) + "] " + getName(sender) + ": " + message);
	}
	
	public static void sendGlobalMessage(CommandSender sender, Object message) {
		Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage("§7[§aG§7] §f" + getName(sender) + ": §7" + message));
		Main.getPlugin().getLogger().info("[GLOBAL] " + getName(sender) + ": " + message);
	}
	
	public static void sendLocalMessage(Player sender, Object message) {
		Predicate<? super CommandSender> filter;
		int matchId;
		if(MatchsManager.instance.playerIsInMatch(sender)) {
			matchId = MatchsManager.instance.getMatchOfPlayer(sender).getId();
			filter = player -> (MatchsManager.instance.playerIsInMatch((Player) player) && MatchsManager.instance.getMatchOfPlayer((Player) player).getId() == matchId) && ((Player) player).getWorld().getName().equals(sender.getWorld().getName()) && ((Player) player).getLocation().distance(sender.getLocation()) <= ConfigManager.instance.getLocalChatDistance();
		} else {
			matchId = -1;
			filter = player -> !MatchsManager.instance.playerIsInMatch((Player) player) && ((Player) player).getWorld().getName().equals(sender.getWorld().getName()) && ((Player) player).getLocation().distance(sender.getLocation()) <= ConfigManager.instance.getLocalChatDistance();
		}
		sendMessageTo(filter, "§e[L] §f" + sender.getName() + ": §e" + message);
		Main.getPlugin().getLogger().info("[LOCAL] [" + (matchId == -1 ? "LOBBY" : "PARTIDA " + matchId) + "] " + sender.getName() + ": " + message);
	}
	
	public static void sendTeamMessage(Capturer sender, Object message) {
		sendMessageTo(player -> (MatchsManager.instance.playerIsInMatch((Player) player) && MatchsManager.instance.getMatchOfPlayer((Player) player).getId() == sender.MATCH.getId()) && sender.MATCH.getCasted((Player) player).getTeam() == sender.getTeam(), sender.getTeam().getColorPreffix() + "[" + sender.getTeam().getPreffix() + "] " + sender.getName() + ": §f" + message);
		Main.getPlugin().getLogger().info("[EQUIPE] [" + sender.getTeam().getPreffix() + "] " + sender.getName() + ": " + message);
	}
	
	public static void sendMessageTo(Predicate<? super CommandSender> filter, Object message) {
		Bukkit.getOnlinePlayers().stream().filter(filter).forEach(player -> player.sendMessage(String.valueOf(message)));
	}
	
	public static void sendMessageToOps(Object message) {
		sendMessageTo(player -> player.isOp(), message);
	}
	
	public static void sendMessageToPlayers(int matchId, Object message) {
		sendMessageTo(player -> MatchsManager.instance.playerIsInMatch((Player) player) && MatchsManager.instance.getMatchOfPlayer((Player) player).getId() == matchId, message);
	}
	
	public static void sendMessageToTeam(int matchId, Team team, Object message) {
		sendMessageTo(player -> MatchsManager.instance.playerIsInMatch((Player) player) && MatchsManager.instance.getMatchOfPlayer((Player) player).getId() == matchId && !MatchsManager.instance.getMatchOfPlayer((Player) player).waitingOrStarting() && MatchsManager.instance.getMatchOfPlayer((Player) player).getPlayers(team).contains(player), message);
	}
	
	private static String getName(CommandSender commandSender) {
		return commandSender instanceof Player ? ((Player) commandSender).getName() : "CONSOLE";
	}
	
}