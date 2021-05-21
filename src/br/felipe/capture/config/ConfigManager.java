package br.felipe.capture.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;

import br.felipe.capture.Main;
import br.felipe.capture.channels.Channel;
import br.felipe.capture.locations.MapTheme;
import br.felipe.capture.locations.NamedLocation;
import br.felipe.capture.locations.RectLocation;
import br.felipe.capture.signs.SignJoin;

public class ConfigManager {
	
	public static ConfigManager instance;
	
	private Main main;
	private FileConfiguration fc;
	
	public ConfigManager(Main main, File file, FileConfiguration fc) {
		this.main = main;
		if(!file.exists()) {
			main.saveDefaultConfig();
			fc.options().copyDefaults(true);
		}
		this.fc = fc;
	}
	
	public boolean autoCreateWorld() {
		return fc.getBoolean("auto_create_world");
	}
	
	public double getLocalChatDistance() {
		return fc.getDouble("local_chat_distance");
	}
	
	public double getRadarDistance() {
		return fc.getDouble("radar_distance");
	}
	
	public int getTotalMatchs() {
		return fc.getInt("total_matchs");
	}
	
	public void addTotalMatch() {
		this.fc.set("total_matchs", getTotalMatchs() + 1);
		this.main.saveConfig();
	}
	
	public boolean autoStartMatch() {
		return fc.getBoolean("auto_start_match");
	}
	
	public boolean autoJoinMatch() {
		return fc.getBoolean("auto_join_match");
	}
	
	public int getMaxPlayersPerMatch() {
		return fc.getInt("game.max_per_match");
	}
	
	public int getMinPlayersToStart() {
		return fc.getInt("game.min_to_start");
	}
	
	public int getTimeToStart() {
		return fc.getInt("game.time_to_start");
	}
	
	public int getMatchTime() {
		return fc.getInt("game.match_time");
	}
	
	public double getAwardBalance() {
		return fc.getDouble("game.award_balance");
	}
	
	public boolean highlight() {
		return fc.getBoolean("game.highlight");
	}
	
	public boolean splitHighlight() {
		return fc.getBoolean("game.split_highlight");
	}
	
	public double getHighLightAward() {
		return fc.getDouble("game.highlight_award");
	}
	
	public boolean hasProfile(String name) {
		return fc.contains("users." + name.toLowerCase());
	}
	
	public void createProfile(String name) {
		this.fc.createSection("users." + name.toLowerCase());
		this.fc.set("users." + name.toLowerCase() + ".balance", 0.0d);
		this.fc.set("users." + name.toLowerCase() + ".matchs", 0);
		this.fc.set("users." + name.toLowerCase() + ".wins", 0);
		this.fc.set("users." + name.toLowerCase() + ".captured", 0);
		this.fc.set("users." + name.toLowerCase() + ".saved", 0);
		this.fc.set("users." + name.toLowerCase() + ".been_captured", 0);
		this.main.saveConfig();
	}
	
	public void removeProfile(String name) {
		this.fc.set("users." + name.toLowerCase(), null);
		this.main.saveConfig();
	}
	
	public double getBalance(String name) {
		return fc.getDouble("users." + name.toLowerCase() + ".balance");
	}
	
	public void setBalance(String name, double balance) {
		if(!hasProfile(name)) {
			this.createProfile(name);
		}
		this.fc.set("users." + name.toLowerCase() + ".balance", balance);
		this.main.saveConfig();
	}
	
	public void withdrawBalance(String name, double balance) {
		this.setBalance(name, getBalance(name) - balance);
	}
	
	public void depositBalance(String name, double balance) {
		this.setBalance(name, getBalance(name) + balance);
	}
	
	public int getCaptured(String name) {
		return fc.getInt("users." + name.toLowerCase() + ".captured");
	}
	
	public void setCaptured(String name, int captured) {
		if(!hasProfile(name)) {
			this.createProfile(name);
		}
		this.fc.set("users." + name.toLowerCase() + ".captured", captured);
		this.main.saveConfig();
	}
	
	public void addCaptured(String name, int captured) {
		this.setCaptured(name, getCaptured(name) + captured);
	}
	
	public int getSaved(String name) {
		return fc.getInt("users." + name.toLowerCase() + ".saved");
	}
	
	public void setSaved(String name, int saved) {
		if(!hasProfile(name)) {
			this.createProfile(name);
		}
		this.fc.set("users." + name.toLowerCase() + ".saved", saved);
		this.main.saveConfig();
	}
	
	public void addSaved(String name, int saved) {
		this.setSaved(name, getSaved(name) + saved);
	}
	
	public int getBeenCaptured(String name) {
		return fc.getInt("users." + name.toLowerCase() + ".been_captured");
	}
	
	public void setBeenCaptured(String name, int beenCaptured) {
		if(!hasProfile(name)) {
			this.createProfile(name);
		}
		this.fc.set("users." + name.toLowerCase() + ".been_captured", beenCaptured);
		this.main.saveConfig();
	}
	
	public void addBeenCaptured(String name) {
		this.setBeenCaptured(name, getBeenCaptured(name) + 1);
	}
	
	public int getWins(String name) {
		return fc.getInt("users." + name.toLowerCase() + ".wins");
	}
	
	public void setWins(String name, int wins) {
		if(!hasProfile(name)) {
			this.createProfile(name);
		}
		this.fc.set("users." + name.toLowerCase() + ".wins", wins);
		this.main.saveConfig();
	}
	
	public void addWin(String name) {
		this.setWins(name, getWins(name) + 1);
	}
	
	public int getMatchs(String name) {
		return fc.getInt("users." + name.toLowerCase() + ".matchs");
	}
	
	public void setMatchs(String name, int matchs) {
		if(!hasProfile(name)) {
			this.createProfile(name);
		}
		this.fc.set("users." + name.toLowerCase() + ".matchs", matchs);
		this.main.saveConfig();
	}
	
	public void addMatch(String name) {
		this.setMatchs(name, getMatchs(name) + 1);
	}
	
	public boolean hasLobbyLocationSetted() {
		return fc.contains("lobby.world");
	}
	
	public Location getLobbyLocation() {
		World world = Bukkit.getWorld(fc.getString("lobby.world"));
		int x = fc.getInt("lobby.x");
		int y = fc.getInt("lobby.y");
		int z = fc.getInt("lobby.z");
		return new Location(world, x, y, z);
	}
	
	public void setLobbyLocation(Location location) {
		this.fc.set("lobby.world", location.getWorld().getName());
		this.fc.set("lobby.x", location.getBlockX());
		this.fc.set("lobby.y", location.getBlockY());
		this.fc.set("lobby.z", location.getBlockZ());
		this.main.saveConfig();
	}
	
	public HashSet<MapTheme> getMaps() {
		HashSet<MapTheme> maps = new HashSet<>();
		for(String mapName : fc.getConfigurationSection("maps").getKeys(false)) {
			String path = "maps." + mapName + ".";
			World world = Bukkit.getWorld(mapName);
			if(world == null) {
				if(autoCreateWorld()) {
					long time = System.currentTimeMillis();
					this.main.getLogger().log(Level.WARNING, "Carregando/criando mundo " + mapName + ". (isso pode levar alguns segundos)");
					Channel.sendMessageToOps("§4Carregando/criando mundo \" + mapName + \". (isso pode levar alguns segundos)");
					world = Bukkit.createWorld(new WorldCreator(mapName));
					this.main.getLogger().log(Level.INFO, "Mundo " + mapName + " carregado/criado com exito. (" + (System.currentTimeMillis() - time) + "ms decorridos)");
					Channel.sendMessageToOps("§7Mundo " + mapName + " carregado/criado com exito. (" + (System.currentTimeMillis() - time) + "ms decorridos)");
				} else {
					this.main.getLogger().log(Level.SEVERE, "O mundo " + mapName + " nao existe ou nao foi carregado.");
					Channel.sendMessageToOps("§4O mundo " + mapName + " nao existe ou nao foi carregado.");
					continue;
				}
			}
			Location spawn = new Location(world, fc.getInt(path + "spawn.x"), fc.getInt(path + "spawn.y"), fc.getInt(path + "spawn.z"));
			HashSet<NamedLocation> namedLocations = new HashSet<>();
			if(fc.contains(path + "locations")) {
				for(String locationName : fc.getConfigurationSection(path + "locations").getKeys(false)) {
					path = "maps." + mapName + ".locations." + locationName + ".";
					String name = fc.getString(path + "name");
					Location location = new Location(world, fc.getDouble(path + "x"), fc.getDouble(path + "y"), fc.getDouble(path + "z"));
					boolean ignoreLayout = fc.getBoolean(path + "ignore_layout");
					boolean roundNumbers = fc.getBoolean(path + "round_numbers");
					namedLocations.add(new NamedLocation(locationName, name, location, ignoreLayout, roundNumbers));
				}
			}
			HashMap<RectLocation, Integer> rectLocations = new HashMap<>();
			if(fc.contains("maps." + mapName + ".rect_locations")) {
				for(String locationName : fc.getConfigurationSection("maps." + mapName + ".rect_locations").getKeys(false)) {
					path = "maps." + mapName + ".rect_locations." + locationName + ".";
					String name = fc.getString(path + "name");
					Location minLocation = new Location(world, fc.getDouble(path + "min_x"), fc.getDouble(path + "min_y"), fc.getDouble(path + "min_z"));
					Location maxLocation = new Location(world, fc.getDouble(path + "max_x"), fc.getDouble(path + "max_y"), fc.getDouble(path + "max_z"));
					boolean ignoreLayout = fc.getBoolean(path + "ignore_layout");
					boolean roundNumbers = fc.getBoolean(path + "round_numbers");
					boolean withBorder = fc.getBoolean(path + "with_border");
					int priority = fc.getInt(path + "priority");
					rectLocations.put(new RectLocation(locationName, name, minLocation, maxLocation, ignoreLayout, roundNumbers, withBorder), priority);
				}
			}
			maps.add(new MapTheme(world, spawn, namedLocations, rectLocations));
		}
		return maps;
	}
	
	public boolean hasMap(String name) {
		return this.getMap(name) != null;
	}
	
	public MapTheme getMap(String name) {
		for(MapTheme map : getMaps()) {
			if(map.getMapName().equals(name)) {
				return map;
			}
		}
		return null;
	}
	
	public MapTheme addMap(World world, Location spawn) {
		String path = "maps." + world.getName();
		this.fc.createSection(path);
		this.fc.set(path + ".spawn.x", spawn.getBlockX());
		this.fc.set(path + ".spawn.y", spawn.getBlockY());
		this.fc.set(path + ".spawn.z", spawn.getBlockZ());
		this.main.saveConfig();
		return new MapTheme(world, spawn, null, null);
	}
	
	public void setSpawnLocation(String name, Location spawn) {
		String path = "maps." + name + ".spawn.";
		this.fc.set(path + "x", spawn.getBlockX());
		this.fc.set(path + "y", spawn.getBlockY());
		this.fc.set(path + "z", spawn.getBlockZ());
		this.main.saveConfig();
	}
	
	public void addNamedLocation(String mapName, String codeName, String name, Location location, boolean ignoreLayout, boolean roundNumbers) {
		String path = "maps." + mapName + ".locations." + codeName + ".";
		this.fc.set(path + "name", name);
		this.fc.set(path + "x", roundNumbers ? location.getBlockX() : location.getX());
		this.fc.set(path + "y", roundNumbers ? location.getBlockY() : location.getY());
		this.fc.set(path + "z", roundNumbers ? location.getBlockZ() : location.getZ());
		this.fc.set(path + "ignore_layout", ignoreLayout);
		this.fc.set(path + "round_numbers", roundNumbers);
		this.main.saveConfig();
	}
	
	public void removeNamedLocation(String mapName, String codeName) {
		String path = "maps." + mapName + ".locations." + codeName;
		this.fc.set(path, null);
		this.main.saveConfig();
	}
	
	public void clearNamedLocations(String mapName) {
		String path = "maps." + mapName + ".locations";
		this.fc.set(path, null);
		this.main.saveConfig();
	}
	
	public void addRectLocation(String mapName, String codeName, String name, Location minLocation, Location maxLocation, boolean ignoreLayout, boolean roundNumbers, boolean withBorder, int priority) {
		String path = "maps." + mapName + ".rect_locations." + codeName + ".";
		this.fc.set(path + "name", name);
		this.fc.set(path + "min_x", roundNumbers ? minLocation.getBlockX() : minLocation.getX());
		this.fc.set(path + "min_y", roundNumbers ? minLocation.getBlockY() : minLocation.getY());
		this.fc.set(path + "min_z", roundNumbers ? minLocation.getBlockZ() : minLocation.getZ());
		this.fc.set(path + "max_x", roundNumbers ? maxLocation.getBlockX() : maxLocation.getX());
		this.fc.set(path + "max_y", roundNumbers ? maxLocation.getBlockY() : maxLocation.getY());
		this.fc.set(path + "max_z", roundNumbers ? maxLocation.getBlockZ() : maxLocation.getZ());
		this.fc.set(path + "ignore_layout", ignoreLayout);
		this.fc.set(path + "round_numbers", roundNumbers);
		this.fc.set(path + "with_border", withBorder);
		this.fc.set(path + "priority", priority);
		this.main.saveConfig();
	}
	
	public void removeRectLocation(String mapName, String codeName) {
		String path = "maps." + mapName + ".rect_locations." + codeName;
		this.fc.set(path, null);
		this.main.saveConfig();
	}
	
	public void clearRectLocations(String mapName) {
		String path = "maps." + mapName + ".rect_locations";
		this.fc.set(path, null);
		this.main.saveConfig();
	}
	
	public void removeMap(String name) {
		this.fc.set("maps." + name, null);
		this.main.saveConfig();
	}
	
	public ArrayList<SignJoin> getSignsJoin() {
		ArrayList<SignJoin> signs = new ArrayList<>();
		for(String signId : fc.getConfigurationSection("signs").getKeys(false)) {
			int id = Integer.valueOf(signId);
			World world = Bukkit.getWorld(fc.getString("signs." + id + ".world"));
			int x = fc.getInt("signs." + id + ".x");
			int y = fc.getInt("signs." + id + ".y");
			int z = fc.getInt("signs." + id + ".z");
			Location location = new Location(world, x, y, z);
			signs.add(new SignJoin(id, location));
		}
		return signs;
	}
	
	public void addSignJoin(SignJoin sign) {
		this.fc.createSection("signs." + sign.getId());
		this.fc.set("signs." + sign.getId() + ".world", sign.getWorld().getName());
		this.fc.set("signs." + sign.getId() + ".x", sign.getX());
		this.fc.set("signs." + sign.getId() + ".y", sign.getY());
		this.fc.set("signs." + sign.getId() + ".z", sign.getZ());
		this.main.saveConfig();
	}
	
	public void removeSignJoin(int id) {
		this.fc.set("signs." + id, null);
		this.main.saveConfig();
	}
	
}