package br.felipe.capture.events;

import org.bukkit.entity.Player;

import br.felipe.capture.game.Match;
import br.felipe.capture.shop.ShopInventory;

public class PlayerOpenShopInventoryEvent extends CEvent {
	
	private Match match;
	
	private Player player;
	private ShopInventory shopInventory;
	
	public PlayerOpenShopInventoryEvent(Match match, Player player, ShopInventory shopInventory) {
		this.match = match;
		this.player = player;
		this.shopInventory = shopInventory;
	}
	
	public Match getMatch() {
		return match;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public ShopInventory getShopInventory() {
		return shopInventory;
	}
	
}