package br.felipe.capture.game.profiles;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import br.felipe.capture.Main;

public class Regeneration implements Listener {
	
	private Capturer player;
	
	private boolean atCombat;
	
	private int secondsLeft;
	
	public Regeneration(Capturer player) {
		this.player = player;
		Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
	}
	
	@EventHandler
	public void onAttack(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player) {
			if(player.MATCH.getCasted((Player) e.getEntity()) == player) {
				if(!atCombat) {
					this.atCombat = true;
					this.secondsLeft = 5;
					new BukkitRunnable() {
						@Override
						public void run() {
							--secondsLeft;
							if(secondsLeft == 0) {
								atCombat = false;
								player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1000000, 0), true);
								this.cancel();
							}
						}
					}.runTaskTimer(Main.getPlugin(), 20l, 20l);
					this.player.removePotionEffect(PotionEffectType.REGENERATION);
				} else {
					this.secondsLeft = 5;
				}
			}
		}
	}
	
	@EventHandler
	public void onRegen(EntityRegainHealthEvent e) {
		if(e.getEntity() instanceof Player) {
			if(player.MATCH.getCasted((Player) e.getEntity()) == player) {
				e.setCancelled(atCombat);
			}
		}
	}
	
	public void unregister() {
		HandlerList.unregisterAll(this);
	}
}