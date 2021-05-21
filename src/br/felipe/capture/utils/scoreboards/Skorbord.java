package br.felipe.capture.utils.scoreboards;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import br.felipe.capture.game.profiles.Capturer;

public class Skorbord {
	
	private Capturer player;
	
	private Scoreboard scoreboard;
	
	private Objective health, sidebar;
	
	private Team blue, red;
	
	private ArrayList<String> carrying;
	
	public Skorbord(Capturer player) {
		this.player = player;
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		this.health = scoreboard.registerNewObjective("health", "health", "§a❤");
		this.health.setDisplaySlot(DisplaySlot.BELOW_NAME);
		this.sidebar = scoreboard.registerNewObjective("sidebar", "dummy", "§6  Carregando  §r");
		this.sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.blue = scoreboard.registerNewTeam("blue");
		this.red = scoreboard.registerNewTeam("red");
		this.blue.setColor(ChatColor.BLUE);
		this.red.setColor(ChatColor.RED);
		this.blue.setOption(Option.COLLISION_RULE, OptionStatus.FOR_OWN_TEAM);
		this.red.setOption(Option.COLLISION_RULE, OptionStatus.FOR_OWN_TEAM);
		if(!player.MATCH.getSettings().showEnemyNametag()) {
			this.blue.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
			this.red.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
		}
		this.player.MATCH.getPlayers().forEach(c -> (c.getTeam() == br.felipe.capture.game.profiles.Team.BLUE ? blue : red).addEntry(c.getName()));
		this.carrying = new ArrayList<>();
	}
	
	public void updateSidebar() {
		int index = 0;
		for(String player : carrying) {
			this.sidebar.getScore(player).setScore(index);
			++index;
		}
	}
	
	public void carryingPlayer(Capturer player) {
		this.carrying.add(player.getTeam().getColorPreffix() + player.getName());
		this.updateSidebar();
	}
	
	public void releasedAll() {
		this.carrying.forEach(entry -> scoreboard.resetScores(entry));
		this.carrying.clear();
	}
	
	public void released(Capturer player) {
		this.carrying.forEach(entry -> scoreboard.resetScores(entry));
		this.carrying.remove(player.getTeam().getColorPreffix() + player.getName());
		this.updateSidebar();
	}
	
	public void playerDisconnected(Capturer player) {
		(player.getTeam() == br.felipe.capture.game.profiles.Team.RED ? red : blue).removeEntry(player.getName());
	}
	
	public Objective getHealth() {
		return health;
	}
	
	public Objective getSidebar() {
		return sidebar;
	}
	
	public Team getBlue() {
		return blue;
	}
	
	public Team getRed() {
		return red;
	}
	
	public Scoreboard getScoreboard() {
		return scoreboard;
	}
	
}