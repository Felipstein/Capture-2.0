package br.felipe.capture.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import br.felipe.capture.game.MatchsManager;
import br.felipe.capture.game.profiles.Capturer;

public class PlayerUtils {
	
	public static final float DEFAULT_WALK_SPEED = 0.2f;
	public static final float DEFAULT_FLY_SPEED = 0.1f;
	public static final double DEFAULT_VALUE_ATTACK_SPEED = 4.0d;
	public static final double DEFAULT_VALUE_KNOCKBACK_RESISTANCE = 0.0d;
	
	public static void clearPotionEffects(Player player) {
		player.getActivePotionEffects().forEach(potion -> player.removePotionEffect(potion.getType()));
	}
	
	public static void cleanPlayer(Player player) {
		player.setFoodLevel(20);
		player.setHealth(20.0d);
		player.setLevel(0);
		player.setExp(0.0f);
		player.setFireTicks(0);
	}
	
	public static void clearInventory(Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.getInventory().setItemInOffHand(null);
	}
	
	public static ArrayList<String> getOnlinePlayersName() {
		ArrayList<String> names = new ArrayList<>();
		Bukkit.getOnlinePlayers().forEach(player -> names.add(player.getName()));
		return names;
	}
	
	public static Set<Player> getPlayersOfMatch(int idMatch) {
		return MatchsManager.instance.getMatch(idMatch).getJustPlayers();
	}
	
	public static Set<Player> getPlayersInMatch() {
		return new HashSet<>(Bukkit.getOnlinePlayers().stream().filter(player -> MatchsManager.instance.playerIsInMatch(player)).collect(Collectors.toList()));
	}
	
	public static Set<Player> getPlayersOutOfMatch() {
		return new HashSet<>(Bukkit.getOnlinePlayers().stream().filter(player -> !MatchsManager.instance.playerIsInMatch(player)).collect(Collectors.toList()));
	}
	
	public static Player getNearestPlayer(Player of, double maxDist) {
		ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers().stream().filter(player -> player.getWorld().getName().equals(of.getWorld().getName()) && player.getLocation().distance(of.getLocation()) <= maxDist && of != player).collect(Collectors.toList()));
		if(players.isEmpty()) {
			return null;
		}
		Player nearest = players.get(0);
		for(Player player : players) {
			if(nearest.getLocation().distance(of.getLocation()) > player.getLocation().distance(of.getLocation())) {
				nearest = player;
			}
		}
		return nearest;
	}
	
	public static Player getNearestPlayer(Capturer of, double maxDist) {
		ArrayList<Capturer> players = new ArrayList<>(of.MATCH.getPlayers().stream().filter(player -> player.getWorld().getName().equals(of.getWorld().getName()) && player.getLocation().distance(of.getLocation()) <= maxDist && of != player && of.getTeam() != player.getTeam()).collect(Collectors.toList()));
		if(players.isEmpty()) {
			return null;
		}
		Capturer nearest = players.get(0);
		for(Capturer player : players) {
			if(nearest.getLocation().distance(of.getLocation()) > player.getLocation().distance(of.getLocation())) {
				nearest = player;
			}
		}
		return nearest;
	}
	
}