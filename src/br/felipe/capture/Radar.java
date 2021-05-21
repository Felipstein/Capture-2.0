package br.felipe.capture;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import br.felipe.capture.config.ConfigManager;
import br.felipe.capture.game.MatchsManager;
import br.felipe.capture.game.profiles.Capturer;
import br.felipe.capture.utils.Formats;
import br.felipe.capture.utils.ItemUtils;
import br.felipe.capture.utils.PlayerUtils;

public class Radar implements Listener {
	
	private Capturer owner;
	
	private ItemStack radar;
	
	public Radar(Capturer owner) {
		this.owner = owner;
		this.radar = ItemUtils.createItemStack(Material.COMPASS, 1, "§dRadar", new ArrayList<>(Arrays.asList(" ", "§7Clique com o botão §fdireito do mouse §7para", "§7rastrear o jogador adversário mais próximo", " ", "§7Obs.: limite máximo de distância: " + ConfigManager.instance.getRadarDistance() + " blocos")), null);
		Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
	}
	
	@EventHandler
	public void onUseRadar(PlayerInteractEvent e) {
		if(e.getItem() == null) {
			return;
		}
		if(!owner.getName().equals(e.getPlayer().getName())) {
			return;
		}
		if(e.getItem().equals(radar) && MatchsManager.instance.playerIsInMatch(e.getPlayer())) {
			if(e.getAction() == Action.RIGHT_CLICK_AIR || (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getHand() == EquipmentSlot.HAND)) {
				e.setCancelled(true);
				if(MatchsManager.instance.getMatchOfPlayer(e.getPlayer()).getCasted(e.getPlayer()).isCaptured()) {
					e.getPlayer().sendMessage("§7Seu radar está apresentando defeitos...");
				} else {
					Player nearest = PlayerUtils.getNearestPlayer(e.getPlayer(), ConfigManager.instance.getRadarDistance());
					if(nearest == null) {
						e.getPlayer().sendMessage("§7Nenhum jogador adversário encontrado dentro de um raio de " + ConfigManager.instance.getRadarDistance() + " bloco" + (ConfigManager.instance.getRadarDistance() > 1 ? "s" : "") + ".");
					} else {
						double distance = nearest.getLocation().distance(e.getPlayer().getLocation());
						e.getPlayer().sendMessage("§7Jogador adversário mais próximo: " + nearest.getName() + ", a " + Formats.decimalFormat(distance) + " bloco" + (distance > 1 ? "s" : "") + ".");
					}
				}
			}
		}
	}
	
	public ItemStack getRadarItem() {
		return radar;
	}
	
}