package br.felipe.capture.locations;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RectLocation {
	
	private String codeName, name;
	private Location minLocation, maxLocation;
	
	private boolean ignoreLayout, roundNumbers, withBorder;
	
	public RectLocation(String codeName, String name, Location minLocation, Location maxLocation, boolean ignoreLayout, boolean roundNumbers, boolean withBorder) {
		this.codeName = codeName;
		this.name = name;
		double minX, minY, minZ;
		double maxX, maxY, maxZ;
		if(minLocation.getX() > maxLocation.getX()) {
			minX = maxLocation.getX();
			maxX = minLocation.getX();
		} else {
			minX = minLocation.getX();
			maxX = maxLocation.getX();
		}
		if(minLocation.getY() > maxLocation.getY()) {
			minY = maxLocation.getY();
			maxY = minLocation.getY();
		} else {
			minY = minLocation.getY();
			maxY = maxLocation.getY();
		}
		if(minLocation.getZ() > maxLocation.getZ()) {
			minZ = maxLocation.getZ();
			maxZ = minLocation.getZ();
		} else {
			minZ = minLocation.getZ();
			maxZ = maxLocation.getZ();
		}
		this.minLocation = new Location(minLocation.getWorld(), minX, minY, minZ);
		this.maxLocation = new Location(maxLocation.getWorld(), maxX, maxY, maxZ);
		this.ignoreLayout = ignoreLayout;
		this.roundNumbers = roundNumbers;
		this.withBorder = withBorder;
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
	
	public boolean withBorder() {
		return withBorder;
	}
	
	public Location getMinLocation() {
		return minLocation;
	}
	
	public Location getMaxLocation() {
		return maxLocation;
	}
	
	public void teleportToCenter(Player player) {
		player.teleport(getCenterLocation());
	}
	
	public Location getCenterLocation() {
		return new Location(minLocation.getWorld(), (maxLocation.getX() + minLocation.getX()) / 2 + 0.5, ignoreLayout ? minLocation.getY() : (maxLocation.getY() + minLocation.getY()) / 2, (maxLocation.getZ() + minLocation.getZ()) / 2 + 0.5);
	}
	
	public double distanceOfCenter(Player player) {
		return this.distanceOfCenter(player.getLocation());
	}
	
	public double distanceOfCenter(Location location) {
		return this.getCenterLocation().distance(location);
	}
	
	public boolean inCenter(Player player, boolean ignoreLayout, boolean roundNumbers) {
		return this.inCenter(player.getLocation(), ignoreLayout, roundNumbers);
	}
	
	public boolean inCenter(Location location, boolean ignoreLayout, boolean roundNumbers) {
		if(roundNumbers) {
			if(ignoreLayout) {
				return (this.getCenterLocation().getBlockX() == location.getBlockX()) && (this.getCenterLocation().getBlockZ() == location.getBlockZ());
			} else {
				return (this.getCenterLocation().getBlockX() == location.getBlockX()) && (this.getCenterLocation().getBlockY() == location.getBlockY()) && (this.getCenterLocation().getBlockZ() == location.getBlockZ());
			}
		} else {
			if(ignoreLayout) {
				return (this.getCenterLocation().getX() == location.getX()) && (this.getCenterLocation().getZ() == location.getZ());
			} else {
				return this.getCenterLocation().equals(location);
			}
		}
	}
	
	public boolean isHovered(Player player) {
		return this.isHovered(player.getLocation());
	}
	
	public boolean isHovered(Location location) {
		return this.isHovered(location, ignoreLayout, roundNumbers, withBorder);
	}
	
	public boolean isHovered(Player player, boolean ignoreLayout, boolean roundNumbers, boolean withBorder) {
		return this.isHovered(player.getLocation(), ignoreLayout, roundNumbers, withBorder);
	}
	
	public boolean isHovered(Location location, boolean ignoreLayout, boolean roundNumbers, boolean withBorder) {
		if(withBorder) {
			if(roundNumbers) {
				if(ignoreLayout) {
					return ((location.getBlockX() >= minLocation.getBlockX()) && (location.getBlockX() <= maxLocation.getBlockX())) && ((location.getBlockZ() >= minLocation.getBlockZ()) && (location.getBlockZ() <= maxLocation.getBlockZ()));
				} else {
					return ((location.getBlockX() >= minLocation.getBlockX()) && (location.getBlockX() <= maxLocation.getBlockX())) && ((location.getBlockY() >= minLocation.getBlockY()) && (location.getBlockY() <= maxLocation.getBlockY())) && ((location.getBlockZ() >= minLocation.getBlockZ()) && (location.getBlockZ() <= maxLocation.getBlockZ()));
				}
			} else {
				if(ignoreLayout) {
					return ((location.getX() >= minLocation.getX()) && (location.getX() <= maxLocation.getX())) && ((location.getZ() >= minLocation.getZ()) && (location.getZ() <= maxLocation.getZ()));
				} else {
					return ((location.getX() >= minLocation.getX()) && (location.getX() <= maxLocation.getX())) && ((location.getY() >= minLocation.getY()) && (location.getY() <= maxLocation.getY())) && ((location.getZ() >= minLocation.getZ()) && (location.getZ() <= maxLocation.getZ()));
				}
			}
		} else {
			if(roundNumbers) {
				if(ignoreLayout) {
					return ((location.getBlockX() > minLocation.getBlockX()) && (location.getBlockX() < maxLocation.getBlockX())) && ((location.getBlockZ() > minLocation.getBlockZ()) && (location.getBlockZ() < maxLocation.getBlockZ()));
				} else {
					return ((location.getBlockX() > minLocation.getBlockX()) && (location.getBlockX() < maxLocation.getBlockX())) && ((location.getBlockY() > minLocation.getBlockY()) && (location.getBlockY() < maxLocation.getBlockY())) && ((location.getBlockZ() > minLocation.getBlockZ()) && (location.getBlockZ() < maxLocation.getBlockZ()));
				}
			} else {
				if(ignoreLayout) {
					return ((location.getX() > minLocation.getX()) && (location.getX() < maxLocation.getX())) && ((location.getZ() > minLocation.getZ()) && (location.getZ() < maxLocation.getZ()));
				} else {
					return ((location.getX() > minLocation.getX()) && (location.getX() < maxLocation.getX())) && ((location.getY() > minLocation.getY()) && (location.getY() < maxLocation.getY())) && ((location.getZ() > minLocation.getZ()) && (location.getZ() < maxLocation.getZ()));
				}
			}
		}
	}
	
}