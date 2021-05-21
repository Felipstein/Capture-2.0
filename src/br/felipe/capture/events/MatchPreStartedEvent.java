package br.felipe.capture.events;

import br.felipe.capture.game.Match;

public class MatchPreStartedEvent extends EEvent {
	
	private Match match;
	
	public MatchPreStartedEvent(Match match) {
		this.match = match;
	}
	
	public Match getMatch() {
		return match;
	}
	
}