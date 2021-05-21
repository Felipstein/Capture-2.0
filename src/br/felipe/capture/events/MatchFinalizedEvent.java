package br.felipe.capture.events;

import br.felipe.capture.game.Match;
import br.felipe.capture.game.profiles.Team;

public class MatchFinalizedEvent extends EEvent {
	
	private Match match;
	private Team winner;
	
	public MatchFinalizedEvent(Match match, Team winner) {
		this.match = match;
		this.winner = winner;
	}
	
	public Match getMatch() {
		return match;
	}
	
	public Team getWinner() {
		return winner;
	}
	
	public void setWinner(Team winner) {
		this.winner = winner;
	}
	
}