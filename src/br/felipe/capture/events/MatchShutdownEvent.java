package br.felipe.capture.events;

import br.felipe.capture.game.Match;

public class MatchShutdownEvent extends EEvent {
	
	private Match match;
	
	public MatchShutdownEvent(Match match) {
		this.match = match;
	}
	
	public Match getMatch() {
		return match;
	}
	
}