package br.felipe.capture.events;

import org.bukkit.entity.Player;

import br.felipe.capture.game.Match;
import br.felipe.capture.shop.ItemBought;
import br.felipe.capture.shop.ShopInventory;

public class PlayerBoughtItemEvent extends CEvent {
	
	private Match match;
	
	private Player player;
	private ShopInventory shopInventory;
	private ItemBought item;
	
	public PlayerBoughtItemEvent(Match match, Player player, ShopInventory shopInventory, ItemBought item) {
		this.match = match;
		this.player = player;
		this.shopInventory = shopInventory;
		this.item = item;
	}
	
	public Match getMatch() {
		return match;
	}
	
	public ItemBought getItem() {
		return item;
	}
	
	public void setItem(ItemBought item) {
		this.item = item;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public ShopInventory getShopInventory() {
		return shopInventory;
	}
	
}