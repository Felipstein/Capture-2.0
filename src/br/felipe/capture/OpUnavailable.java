package br.felipe.capture;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OpUnavailable implements Listener {
	
	private static OpUnavailable a;
	
	private final Set<Player> opUnavailable;
	
	public OpUnavailable() {
		this.opUnavailable = new HashSet<>();
		Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
		a = this;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if(e.getPlayer().isOp()) {
			this.opUnavailable.add(e.getPlayer());
			e.getPlayer().setOp(false);
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if(opUnavailable.contains(e.getPlayer())) {
			this.opUnavailable.remove(e.getPlayer());
			e.getPlayer().setOp(true);
		}
	}
	
	public static boolean available(Player player) {
		if(a.opUnavailable.contains(player)) {
			a.opUnavailable.remove(player);
			player.setOp(true);
			return true;
		}
		return false;
	}
	
}