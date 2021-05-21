package br.felipe.capture.game;

public class Votes {
	
	private int knockbackEnabledVotes, knockbackDisabledVotes;
	private int dayVotes, nightVotes;
	
	public KnockbackVote getKnockbackWinner() {
		if(knockbackEnabledVotes > knockbackDisabledVotes) {
			return KnockbackVote.ENABLED;
		} else if(knockbackEnabledVotes < knockbackDisabledVotes) {
			return KnockbackVote.DISABLED;
		} else {
			return KnockbackVote.TIE;
		}
	}
	
	public TimeVote getTimeWinner() {
		if(dayVotes > nightVotes) {
			return TimeVote.DAY;
		} else if(dayVotes < nightVotes) {
			return TimeVote.NIGHT;
		} else {
			return TimeVote.TIE;
		}
	}
	
	public int getKnockbackEnabledVotes() {
		return knockbackEnabledVotes;
	}
	
	public void setKnockbackEnabledVotes(int knockbackEnabledVotes) {
		this.knockbackEnabledVotes = knockbackEnabledVotes;
	}
	
	public void addKnockbackEnabledVotes(int knockbackEnabledVotes) {
		this.knockbackEnabledVotes += knockbackEnabledVotes;
	}
	
	public int getKnockbackDisabledVotes() {
		return knockbackDisabledVotes;
	}
	
	public void setKnockbackDisabledVotes(int knockbackDisabledVotes) {
		this.knockbackDisabledVotes = knockbackDisabledVotes;
	}
	
	public void addKnockbackDisabledVotes(int knockbackDisabledVotes) {
		this.knockbackDisabledVotes += knockbackDisabledVotes;
	}
	
	public int getDayVotes() {
		return dayVotes;
	}
	
	public void setDayVotes(int dayVotes) {
		this.dayVotes = dayVotes;
	}
	
	public void addDayVotes(int dayVotes) {
		this.dayVotes += dayVotes;
	}
	
	public int getNightVotes() {
		return nightVotes;
	}
	
	public void setNightVotes(int nightVotes) {
		this.nightVotes = nightVotes;
	}
	
	public void addNightVotes(int nightVotes) {
		this.nightVotes += nightVotes;
	}
	
	public enum KnockbackVote {ENABLED, DISABLED, TIE}
	
	public enum TimeVote {DAY, NIGHT, TIE}
	
}