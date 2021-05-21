package br.felipe.capture.signs;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import br.felipe.capture.Main;
import br.felipe.capture.config.ConfigManager;
import br.felipe.capture.game.Match;
import br.felipe.capture.game.MatchsManager;

public class SignJoin implements Listener {
	
	private static ArrayList<SignJoin> signs;
	
	private int id;
	private Sign sign;
	
	public SignJoin(int id, Location location) {
		this(id, (Sign) location.getWorld().getBlockAt(location).getState());
	}
	
	public SignJoin(int id, Sign sign) {
		this.id = id;
		this.sign = sign;
		Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
	}
	
	public int getId() {
		return id;
	}
	
	public Sign getSign() {
		return sign;
	}
	
	public Location getLocation() {
		return sign.getLocation();
	}
	
	public int getX() {
		return sign.getLocation().getBlockX();
	}
	
	public int getY() {
		return sign.getLocation().getBlockY();
	}
	
	public int getZ() {
		return sign.getLocation().getBlockZ();
	}
	
	public Material getMaterial() {
		return sign.getType();
	}
	
	public World getWorld() {
		return sign.getLocation().getWorld();
	}
	public void remove() {
		removeSign(id);
	}
	
	@EventHandler
	public void onJoin(PlayerInteractEvent e) {
		if(e.getClickedBlock() == null) {
			return;
		}
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		if(e.getClickedBlock().getState() instanceof Sign) {
			Sign sign = (Sign) e.getClickedBlock().getState();
			if(hasSignJoin(sign.getLocation())) {
				SignJoin signJoin = getSignJoin(sign.getLocation());
				if(signJoin.id != id) {
					return;
				}
				if(MatchsManager.instance.hasMatch(signJoin.id)) {
					Match match = MatchsManager.instance.getMatch(signJoin.id);
					if(match.waitingOrStarting()) {
						e.getPlayer().sendMessage("§aConectando em §eCapture " + signJoin.id + "§a...");
						if(!match.isFull()) {
							match.joinPlayer(e.getPlayer());
						} else {
							e.getPlayer().sendMessage("§cEssa partida está cheia.");
						}
					} else {
						e.getPlayer().sendMessage("§cEssa partida já está em andamento.");
					}
				} else {
					e.getPlayer().sendMessage("§cEssa partida ainda não foi criada.");
				}
			}
		}
	}
	
	public static boolean hasSignJoin(int id) {
		return getSignJoin(id) != null;
	}
	
	public static boolean hasSignJoin(Location location) {
		return getSignJoin(location) != null;
	}
	
	public static SignJoin getSignJoin(int id) {
		for(SignJoin sign : signs) {
			if(sign.id == id) {
				return sign;
			}
		}
		return null;
	}
	
	public static SignJoin getSignJoin(Location location) {
		for(SignJoin sign : signs) {
			if(sign.getLocation().equals(location)) {
				return sign;
			}
		}
		return null;
	}
	
	public static ArrayList<SignJoin> getSigns() {
		return signs;
	}
	
	public static SignJoin addSign(int id, Location location) {
		return addSign(id, (Sign) location.getWorld().getBlockAt(location).getState());
	}
	
	public static SignJoin addSign(int id, Sign sign) {
		SignJoin signJoin = new SignJoin(id, sign);
		ConfigManager.instance.addSignJoin(signJoin);
		signs.add(signJoin);
		return signJoin;
	}
	
	public static void removeSign(int id) {
		ConfigManager.instance.removeSignJoin(id);
		SignJoin sign = getSignJoin(id);
		signs.remove(sign);
		HandlerList.unregisterAll(sign);
	}
	
	public static void removeSign(Location location) {
		removeSign(getSignJoin(location).getId());
	}
	
	public static void loadSigns() {
		signs = new ArrayList<>();
		ConfigManager.instance.getSignsJoin().forEach(sign -> signs.add(sign));
	}
	
	public static void updateSigns() {
		for(SignJoin sign : signs) {
			Match match = MatchsManager.instance.getMatch(sign.id);
			sign.getSign().setLine(0, "Capture " + (sign.id + 1));
			if(match == null) {
				sign.getSign().setLine(1, "");
				sign.getSign().setLine(2, "§f§l-/-");
				sign.getSign().setLine(3, "§9§lAguardando");
			} else {
				if(match.waitingOrStarting()) {
					sign.getSign().setLine(1, "§aDisponível");
					sign.getSign().setLine(2, "§f§l" + match.getWaitingPlayers().size() + "/" + match.getMaxPlayers());
				} else {
					sign.getSign().setLine(1, "§cEm andamento");
					sign.getSign().setLine(2, "§f§l" + match.getPlayers().size() + "/" + match.getMaxPlayers());
				}
				sign.getSign().setLine(3, "§b§l» ONLINE «");
			}
			sign.getSign().update();
		}
	}
	
}