package br.felipe.capture.locations;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class NamedLocation {
	
	private String codeName, name;
	private Location location;
	
	private boolean ignoreLayout, roundNumbers;
	
	public NamedLocation(String codeName, String name, Location location, boolean ignoreLayout, boolean roundNumbers) {
		this.codeName = codeName;
		this.name = name;
		this.location = location;
		this.ignoreLayout = ignoreLayout;
		this.roundNumbers = roundNumbers;
	}
	
	public String getCodeName() {
		return codeName;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isIgnoreLayout() {
		return ignoreLayout;
	}
	
	public boolean isRoundNumbers() {
		return roundNumbers;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void teleportHere(Player player) {
		player.teleport(location);
	}
	
	public double distanceOf(Player player) {
		return this.distanceOf(player.getLocation());
	}
	
	public double distanceOf(Location location) {
		return this.location.distance(location);
	}
	
	public boolean isHere(Player player) {
		return this.isHere(player.getLocation());
	}
	
	public boolean isHere(Location location) {
		return this.isHere(location, ignoreLayout, roundNumbers);
	}
	
	public boolean isHere(Player player, boolean ignoreLayout, boolean roundNumbers) {
		return this.isHere(player.getLocation(), ignoreLayout, roundNumbers);
	}
	
	public boolean isHere(Location location, boolean ignoreLayout, boolean roundNumbers) {
		if(roundNumbers) {
			if(ignoreLayout) {
				return (this.location.getBlockX() == location.getBlockX()) && (this.location.getBlockZ() == location.getBlockZ());
			} else {
				return (this.location.getBlockX() == location.getBlockX()) && (this.location.getBlockY() == location.getBlockY()) && (this.location.getBlockZ() == location.getBlockZ());
			}
		} else {
			if(ignoreLayout) {
				return (this.location.getX() == location.getX()) && (this.location.getZ() == location.getZ());
			} else {
				return this.location.equals(location);
			}
		}
	}
	
}