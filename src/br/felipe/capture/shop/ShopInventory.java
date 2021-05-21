package br.felipe.capture.shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import br.felipe.capture.Main;
import br.felipe.capture.config.ConfigManager;
import br.felipe.capture.events.PlayerBoughtItemEvent;
import br.felipe.capture.events.PlayerCloseShopInventory;
import br.felipe.capture.events.PlayerOpenShopInventoryEvent;
import br.felipe.capture.game.MatchsManager;
import br.felipe.capture.utils.ItemUtils;

public class ShopInventory implements Listener {
	
	private Inventory inventory;
	private Player player;
	
	private ArrayList<ItemBought> items;
	
	private ShopInventory(Player player) {
		this.inventory = Bukkit.createInventory(null, 9 * 5, "Loja");
		this.player = player;
		this.items = new ArrayList<>();
		this.updateItems();
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.getPlugin());
	}
	
	private void updateItems() {
		if(!items.isEmpty()) {
			this.items.forEach(item -> this.inventory.remove(item.getBought()));
			this.items.clear();
		}
		this.items.add(new ItemBought(9, ItemUtils.enchantItem(ItemUtils.createItemStack(Material.WOODEN_SWORD, 1, getColorName(2) + "Comprar Espada", getLore(2), null), Enchantment.DAMAGE_ALL, 1), ItemUtils.enchantItem(ItemUtils.createItemStack(Material.WOODEN_SWORD, 1, null, null, null), Enchantment.DAMAGE_ALL, 1), 2));
		this.items.add(new ItemBought(10, ItemUtils.enchantItem(ItemUtils.createItemStack(Material.WOODEN_SWORD, 1, getColorName(3.5) + "Comprar Espada", getLore(3.5), null), Enchantment.DAMAGE_ALL, 2), ItemUtils.enchantItem(ItemUtils.createItemStack(Material.WOODEN_SWORD, 1, null, null, null), Enchantment.DAMAGE_ALL, 2), 3.5));
		this.items.add(new ItemBought(18, ItemUtils.enchantItem(ItemUtils.createItemStack(Material.WOODEN_SWORD, 1, getColorName(1.5) + "Comprar Espada", getLore(1.5), null), Enchantment.KNOCKBACK, 1), ItemUtils.enchantItem(ItemUtils.createItemStack(Material.WOODEN_SWORD, 1, null, null, null), Enchantment.KNOCKBACK, 1), 1.5));
		this.items.add(new ItemBought(19, ItemUtils.enchantItem(ItemUtils.createItemStack(Material.WOODEN_SWORD, 1, getColorName(2) + "Comprar Espada", getLore(2), null), Enchantment.KNOCKBACK, 2), ItemUtils.enchantItem(ItemUtils.createItemStack(Material.WOODEN_SWORD, 1, null, null, null), Enchantment.KNOCKBACK, 2), 2));
		this.items.add(new ItemBought(27, ItemUtils.enchantItem(ItemUtils.createItemStack(Material.WOODEN_SWORD, 1, getColorName(2) + "Comprar Espada", getLore(2), null), Enchantment.FIRE_ASPECT, 1), ItemUtils.enchantItem(ItemUtils.createItemStack(Material.WOODEN_SWORD, 1, null, null, null), Enchantment.FIRE_ASPECT, 1), 2));
		this.items.add(new ItemBought(12, ItemUtils.enchantItem(ItemUtils.createItemStack(Material.BOW, 1, getColorName(2.5) + "Comprar Arco", getLore(2.5), null), Enchantment.ARROW_DAMAGE, 1), ItemUtils.enchantItem(ItemUtils.enchantItem(ItemUtils.createItemStack(Material.BOW, 1, null, null, null), Enchantment.ARROW_DAMAGE, 1), Enchantment.ARROW_INFINITE, 1), 2.5));
		this.items.add(new ItemBought(13, ItemUtils.enchantItem(ItemUtils.createItemStack(Material.BOW, 1, getColorName(4.5) + "Comprar Arco", getLore(4.5), null), Enchantment.ARROW_DAMAGE, 2), ItemUtils.enchantItem(ItemUtils.enchantItem(ItemUtils.createItemStack(Material.BOW, 1, null, null, null), Enchantment.ARROW_DAMAGE, 2), Enchantment.ARROW_INFINITE, 1), 4.5));
		this.items.add(new ItemBought(14, ItemUtils.enchantItem(ItemUtils.createItemStack(Material.BOW, 1, getColorName(7) + "Comprar Arco", getLore(7), null), Enchantment.ARROW_DAMAGE, 3), ItemUtils.enchantItem(ItemUtils.enchantItem(ItemUtils.createItemStack(Material.BOW, 1, null, null, null), Enchantment.ARROW_DAMAGE, 3), Enchantment.ARROW_INFINITE, 1), 7));
		this.items.add(new ItemBought(21, ItemUtils.enchantItem(ItemUtils.createItemStack(Material.BOW, 1, getColorName(1) + "Comprar Arco", getLore(1), null), Enchantment.ARROW_KNOCKBACK, 1), ItemUtils.enchantItem(ItemUtils.enchantItem(ItemUtils.createItemStack(Material.BOW, 1, null, null, null), Enchantment.ARROW_KNOCKBACK, 1), Enchantment.ARROW_INFINITE, 1), 1));
		this.items.add(new ItemBought(22, ItemUtils.enchantItem(ItemUtils.createItemStack(Material.BOW, 1, getColorName(2) + "Comprar Arco", getLore(2), null), Enchantment.ARROW_KNOCKBACK, 2), ItemUtils.enchantItem(ItemUtils.enchantItem(ItemUtils.createItemStack(Material.BOW, 1, null, null, null), Enchantment.ARROW_KNOCKBACK, 2), Enchantment.ARROW_INFINITE, 1), 2));
		this.items.add(new ItemBought(30, ItemUtils.enchantItem(ItemUtils.createItemStack(Material.BOW, 1, getColorName(2) + "Comprar Arco", getLore(2), null), Enchantment.ARROW_FIRE, 1), ItemUtils.enchantItem(ItemUtils.enchantItem(ItemUtils.createItemStack(Material.BOW, 1, null, null, null), Enchantment.ARROW_FIRE, 1), Enchantment.ARROW_INFINITE, 1), 2));
		this.items.forEach(item -> this.inventory.setItem(item.getSlot(), item.getBought()));
	}
	
	@EventHandler
	public void onCloseInventory(InventoryCloseEvent e) {
		if(e.getInventory() == inventory && e.getPlayer() == player) {
			new PlayerCloseShopInventory(MatchsManager.instance.getMatchOfPlayer(player), player, this).callEvent();
			HandlerList.unregisterAll(this);
		}
	}
	
	@EventHandler
	public void onClickInventory(InventoryClickEvent e) {
		if(e.getInventory() == inventory && e.getWhoClicked() == player) {
			e.setCancelled(true);
			if(hasItemIn(e.getSlot())) {
				ItemBought item = this.getItemIn(e.getSlot());
				if(getBalance() >= item.getPrice()) {
					PlayerBoughtItemEvent event = new PlayerBoughtItemEvent(MatchsManager.instance.getMatchOfPlayer(player), player, this, item);
					event.callEvent();
					if(event.isCancelled()) {
						return;
					}
					this.player.getInventory().addItem(item.getItem());
					ConfigManager.instance.withdrawBalance(player.getName(), item.getPrice());
					this.player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1.0f, 0.5f);
					this.updateItems();
				}
			}
		}
	}
	
	private double getBalance() {
		return ConfigManager.instance.getBalance(player.getName());
	}
	
	private String getColorName(double price) {
		return price > getBalance() ? "§c" : "§a";
	}
	
	private List<String> getLore(double price) {
		if(price > getBalance()) {
			return new ArrayList<>(Arrays.asList(" ", "§cPreço: $" + price + " coin" + (price > 1 ? "s" : ""), " ", "§cVocê não tem coins suficiente"));
		} else {
			return new ArrayList<>(Arrays.asList(" ", "§aPreço: $" + price + " coin" + (price > 1 ? "s" : "")));
		}
	}
	
	public boolean hasItemIn(int slot) {
		return getItemIn(slot) != null;
	}
	
	public ItemBought getItemIn(int slot) {
		for(ItemBought item : items) {
			if(item.getSlot() == slot) {
				return item;
			}
		}
		return null;
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
	public static InventoryView openInventory(Player player) {
		ShopInventory shopInventory = new ShopInventory(player);
		PlayerOpenShopInventoryEvent event = new PlayerOpenShopInventoryEvent(MatchsManager.instance.getMatchOfPlayer(player), player, shopInventory);
		event.callEvent();
		if(event.isCancelled()) {
			return null;
		}
		return player.openInventory(shopInventory.getInventory());
	}
	
}