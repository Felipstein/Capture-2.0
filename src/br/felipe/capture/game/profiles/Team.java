package br.felipe.capture.game.profiles;

import org.bukkit.Color;

public enum Team {
	
	BLUE(Color.BLUE, "§9", "Azul", "blue-base"),
	RED(Color.RED, "§c", "Vermelho", "red-base");
	
	private Color colorArmor;
	private String colorPreffix, preffix, teamBaseName;
	
	Team(Color colorArmor, String colorPreffix, String preffix, String teamBaseName) {
		this.colorArmor = colorArmor;
		this.colorPreffix = colorPreffix;
		this.preffix = preffix;
		this.teamBaseName = teamBaseName;
	}
	
	public Color getColorArmor() {
		return colorArmor;
	}
	
	public String getColorPreffix() {
		return colorPreffix;
	}
	
	public String getPreffix() {
		return preffix;
	}
	
	public String getTeamBaseName() {
		return teamBaseName;
	}
	
}