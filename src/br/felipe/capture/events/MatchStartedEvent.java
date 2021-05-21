package br.felipe.capture.events;

import br.felipe.capture.game.Match;

public class MatchStartedEvent extends EEvent {
	
	private Match match;
	
	public MatchStartedEvent(Match match) {
		this.match = match;
	}
	
	public Match getMatch() {
		return match;
	}
	
}