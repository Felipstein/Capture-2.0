package br.felipe.capture;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Balance {
	
	public static double getTotal() {
		double total = 0.0d;
		for(double balance : getAllBalances().values()) {
			total += balance;
		}
		return total;
	}
	
	public static Map<String, Double> getAllBalances() {
		Map<String, Double> balances = new HashMap<>();
		for(String name : Main.getPlugin().getConfig().getConfigurationSection("users").getKeys(false)) {
			balances.put(name, Main.getPlugin().getConfig().getDouble("users." + name + ".balance"));
		}
		return balances;
	}
	
	public static Set<String> getRanking() {
		String[] names = new String[getAllBalances().size()];
		double[] balances = new double[getAllBalances().size()];
		int i = 0;
		for(Map.Entry<String, Double> entry : getAllBalances().entrySet()) {
			names[i] = (String) entry.getKey();
			balances[i] = (double) entry.getValue();
			++i;
		}
		for(i = 0; i < names.length; ++i) {
			for(int j = 0; j < balances.length - 1; ++j) {
				if(balances[j] < balances[j + 1]) {
					String aux1 = names[j];
					double aux2 = balances[j];
					names[j] = names[j + 1];
					balances[j] = balances[j + 1];
					names[j + 1] = aux1;
					balances[j + 1] = aux2;
				}
			}
		}
		Set<String> names2 = new HashSet<>();
		for(String name : names) {
			names2.add(name);
		}
		return names2;
	}
	
}