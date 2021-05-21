package br.felipe.capture.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ItemUtils {
	
	public static ItemStack createLeatherArmor(Material material, int amount, int red, int green, int blue, String name, List<String> lore, HashMap<Enchantment, Integer> enchants) {
		return createLeatherArmor(material, amount, Color.fromRGB(red, green, blue), name, lore, enchants);
	}
	
	public static ItemStack createLeatherArmor(Material material, int amount, Color color, String name, List<String> lore, HashMap<Enchantment, Integer> enchants) {
		ItemStack item = createItemStack(material, amount, name, lore, enchants);
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor(color);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack createItemStack(Material material, int amount, String name, List<String> lore, HashMap<Enchantment, Integer> enchants) {
		ItemStack item = new ItemStack(material, amount);
		setName(item, name);
		setLore(item, lore);
		enchantItem(item, enchants);
		return item;
	}
	
	public static ItemStack enchantItem(ItemStack item, HashMap<Enchantment, Integer> enchants) {
		if(enchants != null && !enchants.isEmpty()) {
			for(Enchantment enchant : enchants.keySet()) {
				int level = enchants.get(enchant);
				if(level > enchant.getMaxLevel()) {
					item.addUnsafeEnchantment(enchant, level);
				} else {
					item.addEnchantment(enchant, level);
				}
			}
		}
		return item;
	}
	
	public static ItemStack enchantItem(ItemStack item, Enchantment enchant, int level) {
		if(level > enchant.getMaxLevel()) {
			item.addUnsafeEnchantment(enchant, level);
		} else {
			item.addEnchantment(enchant, level);
		}
		return item;
	}
	
	public static ItemStack setName(ItemStack item, String name) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}
	
	public static String getName(ItemStack item) {
		return item.getItemMeta().getDisplayName();
	}
	
	public static ItemStack setLore(ItemStack item, List<String> lore) {
		if(lore != null && !lore.isEmpty()) {
			ItemMeta meta = item.getItemMeta();
			meta.setLore(lore);
			item.setItemMeta(meta);
		} else {
			item = clearLore(item);
		}
		return item;
	}
	
	public static ItemStack addLore(ItemStack item, String... lore) {
		ItemMeta meta = item.getItemMeta();
		List<String> lore2 = meta.getLore();
		lore2.addAll(new ArrayList<>(Arrays.asList(lore)));
		meta.setLore(lore2);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack clearLore(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		meta.setLore(new ArrayList<>());
		item.setItemMeta(meta);
		return item;
	}
	
	public static List<String> getLore(ItemStack item) {
		return item.getItemMeta().getLore();
	}
	
	public static HashMap<Enchantment, Integer> getEnchantments(String inString){
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		for(String s : inString.split(",")) {
			@SuppressWarnings("deprecation")
			Enchantment enchant = Enchantment.getByName(s.split("=")[0]);
			int level = Integer.valueOf(s.split("=")[1]);
			enchants.put(enchant, level);
		}
		return enchants;
	}
	
	@SuppressWarnings("deprecation")
	public static String enchantmentsToString(HashMap<Enchantment, Integer> enchants) {
		StringBuilder stringBuilder = new StringBuilder();
		for(Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
			Enchantment enchant = (Enchantment) entry.getKey();
			int level = (int) entry.getValue();
			stringBuilder.append("," + enchant.getName() + "=" + level);
		}
		return stringBuilder.toString().substring(1);
	}
	
}