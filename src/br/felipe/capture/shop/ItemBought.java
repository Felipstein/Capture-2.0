package br.felipe.capture.shop;

import org.bukkit.inventory.ItemStack;

public class ItemBought {
	
	private int slot;
	
	private ItemStack bought;
	private ItemStack item;
	
	private double price;
	
	public ItemBought(int slot, ItemStack bought, ItemStack item, double price) {
		this.slot = slot;
		this.bought = bought;
		this.item = item;
		this.price = price;
	}
	
	public int getSlot() {
		return slot;
	}
	
	public ItemStack getBought() {
		return bought;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public double getPrice() {
		return price;
	}
	
}