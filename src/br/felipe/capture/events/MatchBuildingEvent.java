package br.felipe.capture.events;

import br.felipe.capture.game.Match;

public class MatchBuildingEvent extends CEvent {
	
	private Match match;
	private String cancelledReason;
	
	public MatchBuildingEvent(Match match) {
		this.match = match;
	}
	
	public Match getMatch() {
		return match;
	}
	
	public String getCancelledReason() {
		return cancelledReason;
	}
	
	public void setCancelledReason(String cancelledReason) {
		this.cancelledReason = cancelledReason;
	}
	
}