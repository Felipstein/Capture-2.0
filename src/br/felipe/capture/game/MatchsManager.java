package br.felipe.capture.game;

import java.util.HashSet;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import br.felipe.capture.Main;
import br.felipe.capture.config.ConfigManager;
import br.felipe.capture.events.MatchBuildingEvent;
import br.felipe.capture.game.profiles.Team;
import br.felipe.capture.locations.MapTheme;
import br.felipe.capture.signs.SignJoin;

public class MatchsManager {
	
	public static MatchsManager instance;
	
	private Main main;
	
	private HashSet<Match> matchs;
	
	public MatchsManager(Main main) {
		this.main = main;
		this.matchs = new HashSet<>();
	}
	
	public HashSet<Match> getMatchs() {
		return matchs;
	}
	
	public Match addMatch() {
		return this.addMatch(generateFreeId());
	}
	
	public Match addMatch(MapTheme map) {
		return this.addMatch(generateFreeId(), map);
	}
	
	public Match addMatch(int id) {
		if(!MapTheme.hasMapAvailable()) {
			this.main.getLogger().log(Level.SEVERE, "Nao foi possivel inicializar a partida " + id + ": nao ha mapas disponiveis.");
			return null;
		}
		return this.addMatch(id, MapTheme.drawMap(false));
	}
	
	public Match addMatch(int id, MapTheme map) {
		return this.addMatch(id, map, ConfigManager.instance.getMaxPlayersPerMatch(), ConfigManager.instance.getMinPlayersToStart(), ConfigManager.instance.getTimeToStart(), ConfigManager.instance.getMatchTime());
	}
	
	public Match addMatch(int id, MapTheme map, int maxPlayers, int minPlayersToStart, int timeToStart, int matchTime) {
		if(!ConfigManager.instance.hasLobbyLocationSetted()) {
			this.main.getLogger().log(Level.SEVERE, "Nao foi possivel inicializar a partida " + id + ": o lobby nao foi setado.");
			return null;
		}
		if(!map.hasSpawnLocationSetted()) {
			this.main.getLogger().log(Level.SEVERE, "Nao foi possivel inicializar a partida " + id + ": falha ao inicializar o mapa-mundo " + map.getMapName() + ": o spawn nao foi setado.");
			return null;
		}
		if(!map.hasRectLocation(MapTheme.RED_BASE)) {
			this.main.getLogger().log(Level.SEVERE, "Nao foi possivel inicializar a partida " + id + ": falha ao inicializar o mapa-mundo " + map.getMapName() + ": a base vermelha nao foi setada.");
			return null;
		}
		if(!map.hasRectLocation(MapTheme.BLUE_BASE)) {
			this.main.getLogger().log(Level.SEVERE, "Nao foi possivel inicializar a partida " + id + ": falha ao inicializar o mapa-mundo " + map.getMapName() + ": a base azul nao foi setada.");
			return null;
		}
		Match match = new Match(main, id, map, maxPlayers, minPlayersToStart, timeToStart, matchTime);
		this.addMatch(match);
		return match;
	}
	
	public int addMatch(Match match) {
		MatchBuildingEvent e = new MatchBuildingEvent(match);
		if(e.isCancelled()) {
			this.main.getLogger().log(Level.WARNING, "§cPartida " + match.getId() + " cancelada.");
			return -1;
		}
		if(!ConfigManager.instance.hasLobbyLocationSetted()) {
			this.main.getLogger().log(Level.SEVERE, "Nao foi possivel inicializar a partida " + match.getId() + ": o lobby nao foi setado.");
			return -1;
		}
		if(match.getMapTheme() == null) {
			this.main.getLogger().log(Level.SEVERE, "Nao foi possivel inicializar a partida " + match.getId() + ": nenhum mapa foi escolhido.");
			return -1;
		}
		if(!match.getMapTheme().hasSpawnLocationSetted()) {
			this.main.getLogger().log(Level.SEVERE, "Nao foi possivel inicializar a partida " + match.getId() + ": falha ao inicializar o mapa-mundo " + match.getMapName() + ": o spawn nao foi setado.");
			return -1;
		}
		if(!match.getMapTheme().hasRectLocation(MapTheme.RED_BASE)) {
			this.main.getLogger().log(Level.SEVERE, "Nao foi possivel inicializar a partida " + match.getId() + ": falha ao inicializar o mapa-mundo " + match.getMapName() + ": a base vermelha nao foi setada.");
			return -1;
		}
		if(!match.getMapTheme().hasRectLocation(MapTheme.BLUE_BASE)) {
			this.main.getLogger().log(Level.SEVERE, "Nao foi possivel inicializar a partida " + match.getId() + ": falha ao inicializar o mapa-mundo " + match.getMapName() + ": a base azul nao foi setada.");
			return -1;
		}
		MapTheme.makeMapUnavailable(match.getMapName());
		matchs.add(match);
		match.init();
		SignJoin.updateSigns();
		return match.getId();
	}
	
	public void finalizeMatch(int id, Team winner, boolean timeFinished) {
		this.getMatch(id).finalize(winner, timeFinished);
	}
	
	public void shutdownMatch(int id) {
		this.getMatch(id).shutdown();
	}
	
	private int generateFreeId() {
		int id = 0;
		while(hasMatch(id)) {
			++id;
		}
		return id;
	}
	
	public boolean hasMatch(int id) {
		return getMatch(id) != null;
	}
	
	public Match getMatch(int id) {
		for(Match match : matchs) {
			if(match.getId() == id) {
				return match;
			}
		}
		return null;
	}
	
	public boolean playerIsInMatch(Player player) {
		return getMatchOfPlayer(player) != null;
	}
	
	public Match getMatchOfPlayer(Player player) {
		for(Match match : matchs) {
			if(match.hasPlayer(player)) {
				return match;
			}
		}
		return null;
	}
	
	public void disconnectPlayerOfMatch(Player player) {
		this.getMatchOfPlayer(player).disconnectPlayer(player);
	}
	
	public boolean sameMatch(Player... players) {
		if(players.length == 0) {
			return false;
		}
		for(Player player : players) {
			if(!this.playerIsInMatch(player)) {
				return false;
			}
		}
		int matchId = getMatchOfPlayer(players[0]).getId();
		for(Player player : players) {
			if(getMatchOfPlayer(player).getId() != matchId) {
				return false;
			}
		}
		return true;
	}
	
}