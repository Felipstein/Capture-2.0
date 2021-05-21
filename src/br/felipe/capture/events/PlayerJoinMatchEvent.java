package br.felipe.capture.events;

import org.bukkit.entity.Player;

import br.felipe.capture.game.Match;

public class PlayerJoinMatchEvent extends CEvent {
	
	private Match match;
	private Player player;
	
	private String cancelledReason;
	
	public PlayerJoinMatchEvent(Match match, Player player) {
		this.match = match;
		this.player = player;
	}
	
	public Match getMatch() {
		return match;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public String getCancelledReason() {
		return cancelledReason;
	}
	
	public void setCancelledReason(String cancelledReason) {
		this.cancelledReason = cancelledReason;
	}
	
}