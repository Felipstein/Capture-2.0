package br.felipe.capture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.felipe.capture.config.ConfigManager;

public class Ranking {
	
	public static int getTotalWins() {
		int wins = 0;
		for(String name : Main.getPlugin().getConfig().getConfigurationSection("users").getKeys(false)) {
			wins += Main.getPlugin().getConfig().getInt("users." + name + ".wins");
		}
		return wins;
	}
	
	public static int getTotalMatchs() {
		return ConfigManager.instance.getTotalMatchs();
	}
	
	public static Map<String, Integer> getAllWins() {
		Map<String, Integer> wins = new HashMap<>();
		for(String name : Main.getPlugin().getConfig().getConfigurationSection("users").getKeys(false)) {
			wins.put(name, Main.getPlugin().getConfig().getInt("users." + name + ".wins"));
		}
		return wins;
	}
	
	public static List<String> getRanking() {
		String[] names = new String[getAllWins().size()];
		int[] balances = new int[getAllWins().size()];
		int i = 0;
		for(Map.Entry<String, Integer> entry : getAllWins().entrySet()) {
			names[i] = (String) entry.getKey();
			balances[i] = (int) entry.getValue();
			++i;
		}
		for(i = 0; i < names.length; ++i) {
			for(int j = 0; j < balances.length - 1; ++j) {
				if(balances[j] < balances[j + 1]) {
					String aux1 = names[j];
					int aux2 = balances[j];
					names[j] = names[j + 1];
					balances[j] = balances[j + 1];
					names[j + 1] = aux1;
					balances[j + 1] = aux2;
				}
			}
		}
		List<String> names2 = new ArrayList<>();
		for(String name : names) {
			names2.add(name);
		}
		return names2;
	}
	
	public static int getPositionOf(String name) {
		return getRanking().indexOf(name.toLowerCase()) + 1;
	}
	
	public static String getPlayerOf(int position) {
		return getRanking().get(position + 1);
	}
	
}