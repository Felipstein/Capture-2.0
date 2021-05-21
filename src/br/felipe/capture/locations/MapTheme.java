package br.felipe.capture.locations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import br.felipe.capture.config.ConfigManager;
import br.felipe.capture.game.Match;
import br.felipe.capture.game.MatchsManager;
import br.felipe.capture.locations.profile.Builder;

public class MapTheme {
	
	public static final String RED_BASE = "red-base";
	public static final String BLUE_BASE = "blue-base";
	public static final String RED_BASE_NAME = "Base Vermelha";
	public static final String BLUE_BASE_NAME = "Base Azul";
	public static final int MAX_PRIORITY = Integer.MAX_VALUE;
	
	public static Set<MapTheme> mapsLoaded, availableMaps;
	
	private World world;
	private Location spawn;
	
	private HashSet<NamedLocation> namedLocations;
	private HashMap<RectLocation, Integer> rectLocations;
	
	public MapTheme(World world) {
		this(world, null, null, null);
	}
	
	public MapTheme(World world, Location spawn, HashSet<NamedLocation> namedLocations, HashMap<RectLocation, Integer> rectLocations) {
		this.world = world;
		this.spawn = spawn;
		this.namedLocations = namedLocations == null ? new HashSet<>() : namedLocations;
		this.rectLocations = rectLocations == null ? new HashMap<>() : rectLocations;
		this.world.setGameRule(GameRule.MOB_GRIEFING, false);
		this.world.setGameRule(GameRule.DO_FIRE_TICK, false);
	}
	
	public String getMapName() {
		return world.getName();
	}
	
	public World getWorld() {
		return world;
	}
	
	public boolean hasSpawnLocationSetted() {
		return spawn != null;
	}
	
	public Location getSpawnLocation() {
		return spawn;
	}
	
	public void setSpawnLocation(Location location) {
		ConfigManager.instance.setSpawnLocation(world.getName(), location);
		this.spawn = location;
		this.world.setSpawnLocation(location);
	}
	
	public void setSpawnLocation(Player player) {
		this.setSpawnLocation(player.getLocation());
	}
	
	public HashSet<NamedLocation> getNamedLocations() {
		return namedLocations;
	}
	
	public HashMap<RectLocation, Integer> getRectLocationsMap() {
		return rectLocations;
	}
	
	public HashSet<RectLocation> getRectLocations() {
		return new HashSet<>(rectLocations.keySet());
	}
	
	public boolean hasNamedLocation(String codeName) {
		return getNamedLocation(codeName) != null;
	}
	
	public NamedLocation getNamedLocation(String codeName) {
		for(NamedLocation loc : namedLocations) {
			if(loc.getCodeName().equals(codeName)) {
				return loc;
			}
		}
		return null;
	}
	
	public boolean hasNamedLocation(Location location) {
		return this.getNamedLocation(location) != null;
	}
	
	public NamedLocation getNamedLocation(Location location) {
		for(NamedLocation loc : namedLocations) {
			if(loc.isHere(location)) {
				return loc;
			}
		}
		return null;
	}
	
	public boolean hasRectLocation(String codeName) {
		return getRectLocation(codeName) != null;
	}
	
	public RectLocation getRectLocation(String codeName) {
		for(RectLocation loc : getRectLocations()) {
			if(loc.getCodeName().equals(codeName)) {
				return loc;
			}
		}
		return null;
	}
	
	public boolean hasRectLocations(Location location) {
		return !this.getRectLocations(location).isEmpty();
	}
	
	public HashSet<RectLocation> getRectLocations(Location location) {
		HashSet<RectLocation> locations = new HashSet<>();
		for(RectLocation loc : getRectLocations()) {
			if(loc.isHovered(location)) {
				locations.add(loc);
			}
		}
		return locations;
	}
	
	public RectLocation getRectLocation(Location location) {
		RectLocation loc = null;
		for(RectLocation loc2 : getRectLocations(location)) {
			if(getPriorityOfRectLocation(loc2) > getPriorityOfRectLocation(loc)) {
				loc = loc2;
			}
		}
		return loc;
	}
	
	public int getPriorityOfRectLocation(RectLocation rectLocation) {
		if(rectLocation == null) {
			return -1;
		}
		return rectLocations.get(rectLocation);
	}
	
	public int getPriorityOfRectLocation(String codeName) {
		if(codeName == null) {
			return -1;
		}
		return rectLocations.get(getRectLocation(codeName));
	}
	
	public NamedLocation addNamedLocation(String codeName, String name, Location location, boolean ignoreLayout, boolean roundNumbers) {
		ConfigManager.instance.addNamedLocation(world.getName(), codeName, name, location, ignoreLayout, roundNumbers);
		NamedLocation loc = new NamedLocation(codeName, name, location, ignoreLayout, roundNumbers);
		this.namedLocations.add(loc);
		return loc;
	}
	
	public NamedLocation addNamedLocation(String codeName, String name, Player player, boolean ignoreLayout, boolean roundNumbers) {
		return addNamedLocation(codeName, name, player.getLocation(), ignoreLayout, roundNumbers);
	}
	
	public void removeNamedLocation(String codeName) {
		ConfigManager.instance.removeNamedLocation(world.getName(), codeName);
		this.namedLocations.remove(getNamedLocation(codeName));
	}
	
	public RectLocation addRectLocation(int priority, String codeName, String name, Location minLocation, Location maxLocation, boolean ignoreLayout, boolean roundNumbers, boolean withBorder) {
		RectLocation loc = new RectLocation(codeName, name, minLocation, maxLocation, ignoreLayout, roundNumbers, withBorder);
		this.rectLocations.put(loc, priority);
		ConfigManager.instance.addRectLocation(world.getName(), codeName, name, loc.getMinLocation(), loc.getMaxLocation(), ignoreLayout, roundNumbers, withBorder, priority);
		return loc;
	}
	
	public RectLocation addRectLocation(int priority, String codeName, String name, Builder builder, boolean ignoreLayout, boolean roundNumbers, boolean withBorder) {
		return this.addRectLocation(priority, codeName, name, builder.getMinLocation(), builder.getMaxLocation(), ignoreLayout, roundNumbers, withBorder);
	}
	
	public void removeRectLocation(String codeName) {
		ConfigManager.instance.removeRectLocation(world.getName(), codeName);
		this.rectLocations.remove(getRectLocation(codeName));
	}
	
	private static MapTheme getMap(String name) {
		for(MapTheme map : mapsLoaded) {
			if(map.world.getName().equals(name)) {
				return map;
			}
		}
		return null;
	}
	
	private static MapTheme getMap(int index) {
		int i = 0;
		for(MapTheme map : availableMaps) {
			if(i == index) {
				return map;
			}
			++i;
		}
		return null;
	}
	
	public static void loadMaps() {
		availableMaps = new HashSet<>();
		mapsLoaded = new HashSet<>();
		ConfigManager.instance.getMaps().forEach(map -> {
			availableMaps.add(map);
			mapsLoaded.add(map);
		});
	}
	
	public static MapTheme drawMap() {
		return drawMap(true);
	}
	
	public static MapTheme drawMap(boolean makeUnavailable) {
		MapTheme map = getMap(new Random().nextInt(availableMaps.size()));
		if(makeUnavailable) {
			makeMapUnavailable(map.world.getName());
		}
		return map;
	}
	
	public static void loadMap(MapTheme map) {
		mapsLoaded.add(map);
	}
	
	public static void unloadMap(String name) {
		mapsLoaded.remove(getMap(name));
	}
	
	public static void makeMapAvailable(String name) {
		availableMaps.add(getMap(name));
	}
	
	public static void makeMapUnavailable(String name) {
		availableMaps.remove(getMap(name));
	}
	
	public static boolean isAvailable(String name) {
		for(MapTheme map : availableMaps) {
			if(map.world.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean hasMapAvailable() {
		return !availableMaps.isEmpty();
	}
	
	public static int getMatchIdOfMap(String name) {
		for(Match match : MatchsManager.instance.getMatchs()) {
			if(match.getMapName().equals(name)) {
				return match.getId();
			}
		}
		return -1;
	}
	
}