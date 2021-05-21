package br.felipe.capture.locations.profile;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Builder {
	
	private static Set<Builder> builders = new HashSet<>();
	
	private Player player;
	
	private Location minLocation, maxLocation;
	
	public Builder(Player player) {
		this(player, null, null);
	}
	
	public Builder(Player player, Location minLocation, Location maxLocation) {
		this.player = player;
		this.minLocation = minLocation;
		this.maxLocation = maxLocation;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Location getMinLocation() {
		return minLocation;
	}
	
	public void setMinLocation(Location minLocation) {
		this.minLocation = minLocation;
	}
	
	public Location getMaxLocation() {
		return maxLocation;
	}
	
	public void setMaxLocation(Location maxLocation) {
		this.maxLocation = maxLocation;
	}
	
	public boolean hasMinLocationSetted() {
		return minLocation != null;
	}
	
	public boolean hasMaxLocationSetted() {
		return maxLocation != null;
	}
	
	public boolean hasLocationsSetted() {
		return minLocation != null && maxLocation != null;
	}
	
	public void unsetMinLocation() {
		this.minLocation = null;
	}
	
	public void unsetMaxLocation() {
		this.maxLocation = null;
	}
	
	public void stop() {
		stopBuild(player);
	}
	
	public static boolean hasBuilder(Player player) {
		return getBuilder(player) != null;
	}
	
	public static Builder getBuilder(Player player) {
		for(Builder builder : builders) {
			if(builder.getPlayer() == player) {
				return builder;
			}
		}
		return null;
	}
	
	public static Builder startBuild(Player player) {
		Builder builder = new Builder(player);
		builders.add(builder);
		return builder;
	}
	
	public static void stopBuild(Player player) {
		builders.remove(getBuilder(player));
	}
	
}