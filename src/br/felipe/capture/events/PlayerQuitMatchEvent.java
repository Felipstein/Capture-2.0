package br.felipe.capture.events;

import org.bukkit.entity.Player;

import br.felipe.capture.game.Match;

public class PlayerQuitMatchEvent extends EEvent {
	
	private Match match;
	private Player player;
	
	public PlayerQuitMatchEvent(Match match, Player player) {
		this.match = match;
		this.player = player;
	}
	
	public Match getMatch() {
		return match;
	}
	
	public Player getPlayer() {
		return player;
	}
	
}