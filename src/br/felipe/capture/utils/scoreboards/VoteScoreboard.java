package br.felipe.capture.utils.scoreboards;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import br.felipe.capture.game.Match;

public class VoteScoreboard {
	
	private Match match;
	
	private Scoreboard scoreboard;
	private Objective sidebar;
	
	private ArrayList<String> lines;
	
	public VoteScoreboard(Match match) {
		this.match = match;
		this.lines = new ArrayList<>();
		this.loadLines();
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		this.sidebar = scoreboard.registerNewObjective("vote", "dummy", "§2  Votações  §r");
		this.sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.lines.forEach(line -> sidebar.getScore(line).setScore(lines.indexOf(line)));
	}
	
	public void updateVotes() {
		final int enabledVotes = 6, disabledVotes = 5, dayVotes = 2, nightVotes = 1;
		this.setLine(enabledVotes , "§a Ativado    §f(§a" + match.getVotes().getKnockbackEnabledVotes() + " §fvoto" + (match.getVotes().getKnockbackEnabledVotes() > 1 ? "s" : "") + ")");
		this.setLine(disabledVotes, "§c Desativado §f(§a" + match.getVotes().getKnockbackDisabledVotes() + " §fvoto" + (match.getVotes().getKnockbackDisabledVotes() > 1 ? "s" : "") + ")");
		this.setLine(dayVotes     , "§e Dia   §f(§a" + match.getVotes().getDayVotes() + " §fvoto" + (match.getVotes().getDayVotes() > 1 ? "s" : "") + ")");
		this.setLine(nightVotes   , "§9 Noite §f(§a" + match.getVotes().getNightVotes() + " §fvoto" + (match.getVotes().getNightVotes() > 1 ? "s" : "") + ")");
	}
	
	private void loadLines() {
		this.lines.add("   ");								// 0
		this.lines.add("§9 Noite §f(§a0 §fvoto)");			// 1
		this.lines.add("§e Dia   §f(§a0 §fvoto)");			// 2
		this.lines.add("§6Horário:");						// 3
		this.lines.add("  ");								// 4
		this.lines.add("§c Desativado §f(§a0 §fvoto)");		// 5
		this.lines.add("§a Ativado    §f(§a0 §fvoto)");		// 6
		this.lines.add("§6Knockback:");						// 7
		this.lines.add(" ");								// 8
	}
	
	public void setLine(int index, String newLine) {
		String oldLine = this.lines.get(index);
		this.scoreboard.resetScores(oldLine);
		this.sidebar.getScore(newLine).setScore(index);
		this.lines.set(index, newLine);
	}
	
	public Scoreboard getScoreboard() {
		return scoreboard;
	}
	
}