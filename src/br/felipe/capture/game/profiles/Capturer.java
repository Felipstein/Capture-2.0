package br.felipe.capture.game.profiles;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import br.felipe.capture.Main;
import br.felipe.capture.Radar;
import br.felipe.capture.config.ConfigManager;
import br.felipe.capture.game.Match;
import br.felipe.capture.game.Match.MatchStatus;
import br.felipe.capture.locations.RectLocation;
import br.felipe.capture.utils.ActionBar;
import br.felipe.capture.utils.Formats;
import br.felipe.capture.utils.ItemUtils;
import br.felipe.capture.utils.PlayerUtils;
import br.felipe.capture.utils.scoreboards.Skorbord;
import net.minecraft.server.v1_16_R3.EntityPlayer;

public class Capturer extends CraftPlayer implements Player, Listener {
	
	public final Match MATCH;
	
	private Team team;
	
	private ArrayList<Capturer> playersCaptured, capturedBy, playersRescued, rescuedBy;
	private boolean captured;
	
	private ArrayList<Capturer> carrying;
	
	private BukkitTask carryingTask, controlTask;
	
	private Radar radar;
	private Skorbord skorbord;
	
	private String actionBar;
	private long restoreActionBar;
	
	private Regeneration regeneration;
	
	public Capturer(Match match, CraftServer server, EntityPlayer entity) {
		super(server, entity);
		this.MATCH = match;
	}
	
	public Capturer build(Team team) {
		this.team = team;
		this.playersCaptured = new ArrayList<>();
		this.capturedBy = new ArrayList<>();
		this.playersRescued = new ArrayList<>();
		this.rescuedBy = new ArrayList<>();
		this.carrying = new ArrayList<>();
		this.radar = new Radar(this);
		this.actionBar = "";
		this.restoreActionBar = System.currentTimeMillis();
		ItemStack helmet = ItemUtils.createLeatherArmor(Material.LEATHER_HELMET, 1, team.getColorArmor(), team.getColorPreffix() + "Escudo balístico", null, null);
		ItemStack chestplate = ItemUtils.createLeatherArmor(Material.LEATHER_CHESTPLATE, 1, team.getColorArmor(), team.getColorPreffix() + "Escudo balístico", null, null);
		ItemStack leggings = ItemUtils.createLeatherArmor(Material.LEATHER_LEGGINGS, 1, team.getColorArmor(), team.getColorPreffix() + "Escudo balístico", null, null);
		ItemStack boots = ItemUtils.createLeatherArmor(Material.LEATHER_BOOTS, 1, team.getColorArmor(), team.getColorPreffix() + "Escudo balístico", null, null);
		this.getInventory().setArmorContents(new ItemStack[] {boots, leggings, chestplate, helmet});
		if(!getInventory().contains(Material.WOODEN_SWORD)) {
			this.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
		}
		if(!getInventory().contains(Material.BOW)) {
			this.getInventory().addItem(ItemUtils.enchantItem(new ItemStack(Material.BOW), Enchantment.ARROW_INFINITE, 1));
		}
		this.getInventory().addItem(new ItemStack(Material.ARROW));
		if(MATCH.getSettings().containsRadar()) {
			if(getInventory().getItem(7) != null) {
				this.getInventory().addItem(this.radar.getRadarItem());
			} else {
				this.getInventory().setItem(7, this.radar.getRadarItem());
			}
		}
		this.controlTask = new BukkitRunnable() {
			@Override
			public void run() {
				if(MATCH.getStatus() == MatchStatus.ENDING) {
					if(MATCH.getWinner() == team) {
						actionBar = "§eParabéns!";
					} else if(MATCH.getWinner() == null) {
						actionBar = "§6Empate";
					} else {
						actionBar = "§cPerdedores!";
					}
				} else {
					if(System.currentTimeMillis() >= restoreActionBar) {
						if(team == Team.BLUE) {
							actionBar = "§9Azul      §f" + MATCH.getPlayersAlive(Team.BLUE).size() + "§6/§f" + MATCH.getPlayersAlive(Team.RED).size()  + "§c  Vermelho";
						} else {
							actionBar = "§cVermelho  §f" + MATCH.getPlayersAlive(Team.RED).size() +  "§6/§f" + MATCH.getPlayersAlive(Team.BLUE).size() + "§9      Azul";
						}
					}
				}
				if(captured && !isBeingRescued()) {
					RectLocation enemyBase = MATCH.getMapTheme().getRectLocation(MATCH.enemyTeam(Capturer.this).getTeamBaseName());
					if(!enemyBase.isHovered(Capturer.this)) {
						teleport(enemyBase.getCenterLocation());
						sendMessage("§cVocê não pode escapar.");
					}
					setCompassTarget(new Location(getWorld(), getLocation().getX() + (new Random().nextInt(100) - 50), getLocation().getY(), getLocation().getZ() + (new Random().nextInt(100) - 50)));
				} else {
					Player nearest = PlayerUtils.getNearestPlayer(Bukkit.getPlayer(getName()), ConfigManager.instance.getRadarDistance());
					if(nearest != null) {
						setCompassTarget(nearest.getLocation());
					}
					if(isBeingCarrying() && !isInsideVehicle()) {
						Capturer who = whoIsCarrying();
						int position = who.getCarrying().indexOf(Capturer.this);
						Capturer below;
						if(position == 0) {
							below = who;
						} else {
							below = who.getCarrying().get(position - 1);
						}
						below.setPassenger(Capturer.this);
						sendMessage("§cVocê não pode escapar.");
					}
				}
				setPlayerListName((captured ? "§4* " : "") + team.getColorPreffix() + getName() + " §f" + getPlayersCaptured().size() + "§7/§f" + getCapturedBy().size());
				sendActionBar(actionBar);
			}
		}.runTaskTimer(Main.getPlugin(), 1l, 1l);
		if(MATCH.getSettings().advancedRegen()) {
			this.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1000000, 0), true);
			this.regeneration = new Regeneration(this);
		}
		Main.getPlugin().getServer().getPluginManager().registerEvents(this, Main.getPlugin());
		return this;
	}
	
	public void unregister() {
		if(controlTask != null) {
			this.controlTask.cancel();
			this.controlTask = null;
		}
		HandlerList.unregisterAll(this);
		this.regeneration.unregister();
	}
	
	public Team getTeam() {
		return team;
	}
	
	public ArrayList<Capturer> getPlayersCaptured() {
		return playersCaptured;
	}
	
	public ArrayList<Capturer> getCapturedBy() {
		return capturedBy;
	}
	
	public ArrayList<Capturer> getPlayersRescued() {
		return playersRescued;
	}
	
	public ArrayList<Capturer> getRescuedBy() {
		return rescuedBy;
	}
	
	public Regeneration getRegeneration() {
		return regeneration;
	}
	
	public boolean isCaptured() {
		return captured;
	}
	
	public void wasCaptured() {
		this.captured = true;
		this.teleport(MATCH.getMapTheme().getRectLocation(team.getTeamBaseName()).getCenterLocation());
		this.sendMessage("§cVocê foi capturado.");
		this.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS , 1000000, 0), true);
		this.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 0), true);
	}
	
	public void wasRescued() {
		this.captured = false;
		this.removePotionEffect(PotionEffectType.BLINDNESS);
		this.removePotionEffect(PotionEffectType.SLOW);
		this.sendMessage("§aVocê foi salvo.");
		this.removePotionEffect(PotionEffectType.BLINDNESS);
		this.removePotionEffect(PotionEffectType.SLOW);
	}
	
	public ArrayList<Capturer> getCarrying() {
		return carrying;
	}
	
	public void carry(Capturer player) {
		if(carrying.contains(player)) {
			return;
		}
		if(player.isCarrying()) {
			player.releaseAll();
		}
		boolean empty = carrying.isEmpty();
		Capturer top = this;
		for(Capturer c : carrying) {
			top = c;
		}
		top.setPassenger(player);
		this.hidePlayer(player);
		this.carrying.add(player);
		this.skorbord.carryingPlayer(player);
		if(empty) {
			this.carryingTask = new BukkitRunnable() {
				int secondsLeft = 61;
				@Override
				public void run() {
					--secondsLeft;
					if(secondsLeft == -1 || carrying.isEmpty()) {
						releaseAll();
						setLevel(0);
						setExp(0.0f);
						this.cancel();
						return;
					}
					float percent = (secondsLeft * 100.0f) / 60.0f;
					setLevel(secondsLeft);
					setExp(percent / 100.0f);
					carrying.forEach(carryied -> {
						carryied.setLevel(secondsLeft);
						carryied.setExp(percent / 100.0f);
					});
				}
			}.runTaskTimer(Main.getPlugin(), 0l, 20l);
		}
		if(player.getTeam() != team) {
			player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 0.5f);
		}
		this.playSound(getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
	}
	
	public boolean isCarrying() {
		return !carrying.isEmpty();
	}
	
	public boolean isCarrying(Capturer player) {
		return carrying.contains(player);
	}
	
	public boolean isCapturing(Capturer player) {
		return isCarrying(player) ? team != player.getTeam() : false;
	}
	
	public boolean isHelping(Capturer player) {
		return isCarrying(player) ? team == player.getTeam() : false;
	}
	
	public void release(Capturer player) {
		if(isCarrying(player)) {
			this.skorbord.released(player);
			int position = carrying.indexOf(player);
			if(position == carrying.size() - 1) {
				this.carrying.remove(position);
				return;
			}
			Capturer above = carrying.get(position + 1);
			if(position == 0) {
				this.setPassenger(above);
			} else {
				carrying.get(position - 1).setPassenger(above);
			}
			this.carrying.remove(position);
			this.showPlayer(player);
			player.setLevel(0);
			player.setExp(0.0f);
		}
	}
	
	public void releaseAll() {
		if(carrying.isEmpty()) {
			return;
		}
		this.carryingTask.cancel();
		this.carryingTask = null;
		this.carrying.forEach(player -> {
			player.leaveVehicle();
			this.showPlayer(player);
			player.setLevel(0);
			player.setExp(0.0f);
		});
		this.carrying.clear();
		this.setLevel(0);
		this.skorbord.releasedAll();
	}
	
	public boolean isBeingCarrying() {
		return whoIsCarrying() != null;
	}
	
	public boolean isBeingCaptured() {
		Capturer who = whoIsCarrying();
		return who != null ? team != who.getTeam() : false;
	}
	
	public boolean isBeingRescued() {
		Capturer who = whoIsCarrying();
		return who != null ? team == who.getTeam() : false;
	}
	
	public Capturer whoIsCarrying() {
		for(Capturer player : MATCH.getPlayers()) {
			if(player.isCarrying(this)) {
				return player;
			}
		}
		return null;
	}
	
	public BukkitTask getCarryingTask() {
		return carryingTask;
	}
	
	public BukkitTask getControlTask() {
		return controlTask;
	}
	
	public void setAttackSpeedAttribute(double value) {
		AttributeInstance a = this.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
		a.setBaseValue(value);
		this.saveData();
	}
	
	@EventHandler
	public void onIsAttacked(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			if(MATCH.hasPlayer((Player) e.getEntity())) {
				if(MATCH.getCasted((Player) e.getEntity()) == this) {
					if(captured || isBeingCarrying()) {
						e.setCancelled(true);
						return;
					}
					Player player = (Player) e.getEntity();
					if(e.getCause() == DamageCause.LAVA && !MATCH.getSettings().isVulnerableLava()) {
						e.setCancelled(true);
						return;
					} else if(e.getCause() == DamageCause.FALL && !MATCH.getSettings().isVulnerableFall()) {
						e.setCancelled(true);
						return;
					} else if(e.getCause() == DamageCause.DROWNING && !MATCH.getSettings().isVulnerableDrowning()) {
						e.setCancelled(true);
						return;
					} else if(e.getCause() == DamageCause.PROJECTILE) {
						return;
					} else if(e.getCause() == DamageCause.VOID) {
						MATCH.getCasted((Player) e.getEntity()).wasCaptured();
						MATCH.sendMessageToPlayers(MATCH.getCasted((Player) e.getEntity()).getTeam().getColorPreffix() + ((Player) e.getEntity()).getName() + " §6foi capturado pelos piratas maconheiros do submundo!");
						e.setCancelled(true);
						return;
					}
					if(e.getCause() != DamageCause.ENTITY_ATTACK) {
						e.setDamage(player.getHealth() - (player.getHealth() - 1));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onIsAttacked2(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player && !(e.getDamager() instanceof Player)) {
			if(MATCH.hasPlayer((Player) e.getEntity())) {
				if(MATCH.getCasted((Player) e.getEntity()) == this) {
					if(!MATCH.getSettings().isVulnerableEntities()) {
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onAttack(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player) {
			if(MATCH.hasPlayer((Player) e.getDamager())) {
				if(MATCH.getCasted((Player) e.getDamager()) == this) {
					if(captured || isBeingCarrying()) {
						e.setCancelled(true);
						return;
					}
					if(e.getEntity() instanceof Player) {
						Player damager = (Player) e.getDamager();
						Player player = (Player) e.getEntity();
						if(MATCH.sameTeam(MATCH.getCasted(damager), MATCH.getCasted(player))) {
							return;
						}
						Capturer c = MATCH.getCasted(player);
						if(!e.isCancelled() && (!c.isBeingCarrying() && !captured)) {
							this.restoreActionBar = System.currentTimeMillis() + 3000l;
							double percent = ((player.getHealth() - e.getDamage()) * 100.0d) / 20.0d;
							this.actionBar = c.getTeam().getColorPreffix() + c.getName() + "§f   " + Formats.decimalFormat(percent) + "% " + (percent < 45 ? "§4" : "§c") + "❤";
						}
					}
				}
			}
		} else if(e.getDamager() instanceof Arrow) {
			if(((Arrow) e.getDamager()).getShooter() instanceof Player) {
				Player damager = (Player) ((Arrow) e.getDamager()).getShooter();
				if(e.getEntity() instanceof Player) {
					Player player = (Player) e.getEntity();
					if(MATCH.sameTeam(MATCH.getCasted(damager), MATCH.getCasted(player))) {
						return;
					}
					if(MATCH.getCasted(damager) != this) {
						return;
					}
					Capturer c = MATCH.getCasted(player);
					if(!e.isCancelled() && (!c.isBeingCarrying() && !captured)) {
						this.restoreActionBar = System.currentTimeMillis() + 3000l;
						double percent = ((player.getHealth() - e.getDamage()) * 100.0d) / 20.0d;
						this.actionBar = c.getTeam().getColorPreffix() + c.getName() + "§f   " + Formats.decimalFormat(percent) + "% §4❤";
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onShoot(EntityShootBowEvent e) {
		if(e.getEntity() instanceof Player) {
			if(MATCH.hasPlayer((Player) e.getEntity())) {
				if(MATCH.getCasted((Player) e.getEntity()) == this) {
					if(captured || isBeingCarrying()) {
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	public Radar getRadar() {
		return radar;
	}
	
	public void setScoreboard(Skorbord skorbord) {
		this.skorbord = skorbord;
		this.setScoreboard(skorbord.getScoreboard());
	}
	
	public Skorbord getSkorbord() {
		return skorbord;
	}
	
	public void sendActionBar(Object actionBar) {
		ActionBar.sendActionBar(this, String.valueOf(actionBar));
	}
	
	@Override
	public void hidePlayer(Player player) {
		Bukkit.getPlayer(getName()).hidePlayer(Main.getPlugin(), player);
	}

	@Override
	public void showPlayer(Player player) {
		Bukkit.getPlayer(getName()).showPlayer(Main.getPlugin(), player);
	}
	
	@Override
	public void playSound(Location loc, Sound sound, float volume, float pitch) {
		Bukkit.getPlayer(getName()).playSound(loc, sound, volume, pitch);
	}
	
	@Override
	public Location getLocation() {
		return Bukkit.getPlayer(getName()).getLocation();
	}
	
	public void teleportToBase() {
		this.teleport(MATCH.getMapTheme().getRectLocation(team.getTeamBaseName()).getCenterLocation());
	}
	
	public static Capturer cast(Match match, Player player) {
		return new Capturer(match, (CraftServer) player.getServer(), ((CraftPlayer) player).getHandle());
	}
	
}