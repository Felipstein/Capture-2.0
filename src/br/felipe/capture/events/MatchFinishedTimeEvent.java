package br.felipe.capture.events;

import br.felipe.capture.game.Match;

public class MatchFinishedTimeEvent extends EEvent {
	
	private Match match;
	
	public MatchFinishedTimeEvent(Match match) {
		this.match = match;
	}
	
	public Match getMatch() {
		return match;
	}
	
}