package br.felipe.capture.game;

import org.bukkit.Difficulty;

import br.felipe.capture.config.ConfigManager;
import br.felipe.capture.utils.PlayerUtils;

public class Settings {
	
	private double genericAttackSpeed;
	private double genericKnockbackResistence;
	private float playersWalkSpeed;
	private float playersFlySpeed;
	private boolean showEnemyNametag;
	
	private boolean vulnerableDrowning;
	private boolean vulnerableLava;
	private boolean vulnerableFall;
	private boolean vulnerableEntities;
	
	private Difficulty difficulty;
	
	private boolean invincibleEntities;
	private long tasksTimeDelay;
	private boolean messages;
	
	private boolean noPlayersToLose;
	private boolean radar, shop;
	
	private boolean headshot;
	private double yDiffHeadshot;
	private double headshotPercentMoreDamage;
	
	private boolean highlight;
	private boolean splitHighlight;
	private double highlightAward;
	private boolean advancedRegen;
	
	private boolean rains;
	private long time;
	private boolean doDaylightCycle;
	private boolean spawnEntities;
	
	private boolean sounds;
	
	private Settings() {}
	
	public Settings(double genericAttackSpeed, double genericKnockbackResistence, float playersWalkSpeed, float playersFlySpeed, boolean showEnemyNameTag, boolean vulnerableDrowning, boolean vulnerableLava, boolean vulnerableFall, boolean vulnerableEntities, Difficulty difficulty, boolean invincibleEntities, long tasksTimeDelay, boolean messages, boolean noPlayersToLose, boolean radar, boolean shop, boolean headshot, double yDiffHeadshot, double headshotPercentMoreDamage, boolean highlight, boolean splitHighlight, double highlightAward, boolean advancedRegen, boolean rains, long time, boolean doDaylightCycle, boolean spawnEntities, boolean sounds) {
		this.genericAttackSpeed = genericAttackSpeed;
		this.genericKnockbackResistence = genericKnockbackResistence;
		this.playersWalkSpeed = playersWalkSpeed;
		this.playersFlySpeed = playersFlySpeed;
		this.showEnemyNametag = showEnemyNameTag;
		this.vulnerableDrowning = vulnerableDrowning;
		this.vulnerableLava = vulnerableLava;
		this.vulnerableFall = vulnerableFall;
		this.vulnerableEntities = vulnerableEntities;
		this.difficulty = difficulty;
		this.invincibleEntities = invincibleEntities;
		this.tasksTimeDelay = tasksTimeDelay;
		this.messages = messages;
		this.noPlayersToLose = noPlayersToLose;
		this.radar = radar;
		this.shop = shop;
		this.headshot = headshot;
		this.yDiffHeadshot = yDiffHeadshot;
		this.headshotPercentMoreDamage = headshotPercentMoreDamage;
		this.highlight = highlight;
		this.splitHighlight = splitHighlight;
		this.highlightAward = highlightAward;
		this.advancedRegen = advancedRegen;
		this.rains = rains;
		this.time = time;
		this.doDaylightCycle = doDaylightCycle;
		this.spawnEntities = spawnEntities;
		this.sounds = sounds;
	}
	
	public double getGenericAttackSpeed() {
		return genericAttackSpeed;
	}

	public void setGenericAttackSpeed(double genericAttackSpeed) {
		this.genericAttackSpeed = genericAttackSpeed;
	}
	
	public double getGenericKnockbackResistence() {
		return genericKnockbackResistence;
	}

	public void setGenericKnockbackResistence(double genericKnockbackResistence) {
		this.genericKnockbackResistence = genericKnockbackResistence;
	}

	public float getPlayersWalkSpeed() {
		return playersWalkSpeed;
	}

	public void setPlayersWalkSpeed(float playersWalkSpeed) {
		this.playersWalkSpeed = playersWalkSpeed;
	}

	public float getPlayersFlySpeed() {
		return playersFlySpeed;
	}

	public void setPlayersFlySpeed(float playersFlySpeed) {
		this.playersFlySpeed = playersFlySpeed;
	}
	
	public boolean isVulnerableDrowning() {
		return vulnerableDrowning;
	}

	public void setVulnerableDrowning(boolean vulnerableDrowning) {
		this.vulnerableDrowning = vulnerableDrowning;
	}

	public boolean isVulnerableLava() {
		return vulnerableLava;
	}

	public void setVulnerableLava(boolean vulnerableLava) {
		this.vulnerableLava = vulnerableLava;
	}

	public boolean isVulnerableFall() {
		return vulnerableFall;
	}

	public void setVulnerableFall(boolean vulnerableFall) {
		this.vulnerableFall = vulnerableFall;
	}

	public boolean isVulnerableEntities() {
		return vulnerableEntities;
	}

	public void setVulnerableEntities(boolean vulnerableEntities) {
		this.vulnerableEntities = vulnerableEntities;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}
	
	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	public boolean isInvincibleEntities() {
		return invincibleEntities;
	}

	public void setInvincibleEntities(boolean invincibleEntities) {
		this.invincibleEntities = invincibleEntities;
	}

	public long getTasksTimeDelay() {
		return tasksTimeDelay;
	}

	public void setTasksTimeDelay(long tasksTimeDelay) {
		this.tasksTimeDelay = tasksTimeDelay;
	}

	public boolean isMessages() {
		return messages;
	}

	public boolean messagesEnabled() {
		return messages;
	}

	public void setMessages(boolean messages) {
		this.messages = messages;
	}
	
	public boolean noPlayersToLose() {
		return noPlayersToLose;
	}

	public void setNoPlayersToLose(boolean noPlayersToLose) {
		this.noPlayersToLose = noPlayersToLose;
	}
	
	public boolean containsRadar() {
		return radar;
	}

	public void setRadar(boolean radar) {
		this.radar = radar;
	}

	public boolean containsShop() {
		return shop;
	}

	public void setShop(boolean shop) {
		this.shop = shop;
	}
	
	public boolean showEnemyNametag() {
		return showEnemyNametag;
	}

	public void setShowEnemyNametag(boolean showEnemyNametag) {
		this.showEnemyNametag = showEnemyNametag;
	}
	
	public boolean headshotsEnabled() {
		return headshot;
	}

	public void setHeadshot(boolean headshot) {
		this.headshot = headshot;
	}

	public double getYDiffHeadshot() {
		return yDiffHeadshot;
	}

	public void setYDiffHeadshot(double yDiffHeadshot) {
		this.yDiffHeadshot = yDiffHeadshot;
	}

	public double getHeadshotPercentMoreDamage() {
		return headshotPercentMoreDamage;
	}

	public void setHeadshotPercentMoreDamage(double headshotPercentMoreDamage) {
		this.headshotPercentMoreDamage = headshotPercentMoreDamage;
	}
	
	public boolean containsHighlight() {
		return highlight;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}

	public boolean isSplitHighlight() {
		return splitHighlight;
	}

	public void setSplitHighlight(boolean splitHighlight) {
		this.splitHighlight = splitHighlight;
	}

	public double getHighlightAward() {
		return highlightAward;
	}

	public void setHighlightAward(double highlightAward) {
		this.highlightAward = highlightAward;
	}

	public boolean canRains() {
		return rains;
	}
	
	public void setRains(boolean rains) {
		this.rains = rains;
	}
	
	public long getTimeOfWorld() {
		return time;
	}
	
	public void setTimeOfWorld(long time) {
		this.time = time;
	}
	
	public boolean doDaylightCycle() {
		return doDaylightCycle;
	}
	
	public void setDoDaylightCycle(boolean doDaylightCycle) {
		this.doDaylightCycle = doDaylightCycle;
	}
	
	public boolean canSpawnEntities() {
		return spawnEntities;
	}
	
	public void setSpawnEntities(boolean spawnEntities) {
		this.spawnEntities = spawnEntities;
	}
	
	public boolean containsSounds() {
		return sounds;
	}
	
	public void setSounds(boolean sounds) {
		this.sounds = sounds;
	}
	
	public boolean advancedRegen() {
		return advancedRegen;
	}
	
	public void setAdvancedRegen(boolean advancedRegen) {
		this.advancedRegen = advancedRegen;
	}
	
	public static Settings defaultSettings() {
		Settings settings = new Settings();
		settings.genericAttackSpeed = 24.0d;
		settings.genericKnockbackResistence = PlayerUtils.DEFAULT_VALUE_KNOCKBACK_RESISTANCE;
		settings.playersWalkSpeed = PlayerUtils.DEFAULT_WALK_SPEED;
		settings.playersFlySpeed = PlayerUtils.DEFAULT_FLY_SPEED;
		settings.showEnemyNametag = true;
		settings.vulnerableDrowning = true;
		settings.vulnerableEntities = true;
		settings.vulnerableFall = false;
		settings.vulnerableLava = true;
		settings.difficulty = Difficulty.PEACEFUL;
		settings.invincibleEntities = false;
		settings.tasksTimeDelay = 20l;
		settings.messages = true;
		settings.noPlayersToLose = true;
		settings.radar = true;
		settings.shop = true;
		settings.headshot = false;
		settings.yDiffHeadshot = 1.7d;
		settings.headshotPercentMoreDamage = 27.5d;
		settings.highlight = ConfigManager.instance.highlight();
		settings.splitHighlight = ConfigManager.instance.splitHighlight();
		settings.highlightAward = ConfigManager.instance.getHighLightAward();
		settings.advancedRegen = true;
		settings.rains = false;
		settings.time = 1000l;
		settings.doDaylightCycle = true;
		settings.spawnEntities = true;
		settings.sounds = true;
		return settings;
	}
	
}