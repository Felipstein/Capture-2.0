package br.felipe.capture.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import br.felipe.capture.Main;
import br.felipe.capture.channels.Channel;
import br.felipe.capture.config.ConfigManager;
import br.felipe.capture.events.MatchFinalizedEvent;
import br.felipe.capture.events.MatchFinishedTimeEvent;
import br.felipe.capture.events.MatchPreStartedEvent;
import br.felipe.capture.events.MatchShutdownEvent;
import br.felipe.capture.events.MatchStartedEvent;
import br.felipe.capture.events.PlayerJoinMatchEvent;
import br.felipe.capture.events.PlayerQuitMatchEvent;
import br.felipe.capture.game.Votes.KnockbackVote;
import br.felipe.capture.game.Votes.TimeVote;
import br.felipe.capture.game.profiles.Capturer;
import br.felipe.capture.game.profiles.Team;
import br.felipe.capture.locations.MapTheme;
import br.felipe.capture.locations.RectLocation;
import br.felipe.capture.shop.ShopInventory;
import br.felipe.capture.signs.SignJoin;
import br.felipe.capture.utils.Formats;
import br.felipe.capture.utils.ItemUtils;
import br.felipe.capture.utils.PlayerUtils;
import br.felipe.capture.utils.scoreboards.Skorbord;
import br.felipe.capture.utils.scoreboards.VoteScoreboard;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

@SuppressWarnings("deprecation")
public class Match implements Listener {
	
	public enum MatchStatus {WAITING, STARTING, RUNNING, ENDING}
	private MatchStatus status;
	
	private final MatchsManager manager;
	
	private final Main main;
	private final int id;
	
	private final MapTheme map;
	private Team winner;
	
	private BossBar b_waitingPlayers;
	private BossBar b_waitingTime;
	private BossBar b_leftTime;
	
	private VoteScoreboard voteScoreboard;
	private Votes votes;
	private Set<Player> knockbackEnabledVoted, knockbackDisabledVoted, timeDayVoted, timeNightVoted;
	
	private final int maxPlayers;
	private final Set<Player> waitingPlayers;
	private final Set<Capturer> players;
	
	private final int minPlayersToStart;
	private BukkitTask preStartControl, preStartTime;
	private final int timeToStart;
	private int leftTimeToStart;
	
	private BukkitTask matchControl, matchTime;
	private int matchLeftTime;
	
	private boolean moveReleased;
	
	private Settings settings;
	
	private ItemStack shopItem;
	
	public Match(Main main, int id, MapTheme map, int maxPlayers, int minPlayersToStart, int timeToStart, int matchTime) {
		this(main, id, map, maxPlayers, minPlayersToStart, timeToStart, matchTime, Settings.defaultSettings());
	}
	
	public Match(Main main, int id, MapTheme map, int maxPlayers, int minPlayersToStart, int timeToStart, int matchTime, Settings settings) {
		this.status = MatchStatus.WAITING;
		this.manager = MatchsManager.instance;
		this.main = main;
		this.id = id;
		this.map = map;
		this.maxPlayers = maxPlayers;
		this.waitingPlayers = new HashSet<>();
		this.players = new HashSet<>();
		this.minPlayersToStart = minPlayersToStart;
		this.timeToStart = timeToStart;
		this.leftTimeToStart = timeToStart;
		this.matchLeftTime = matchTime;
		this.settings = settings;
		this.shopItem = ItemUtils.createItemStack(Material.BOOK, 1, "§6Loja", new ArrayList<>(Arrays.asList(" ", "§7Clique com o botão §fdireito do mouse §7ou", "§7digite o comando §f/loja §7para acessar a loja do jogo", " ")), null);
		this.configureBossBars();
		Bukkit.getPluginManager().registerEvents(this, main);
	}
	
	private void configureBossBars() {
		this.b_waitingPlayers = main.getServer().createBossBar("§9Aguarde §b" + (minPlayersToStart - waitingPlayers.size()) + " jogadores §9para iniciar o cronometro", BarColor.BLUE, BarStyle.SEGMENTED_10);
		this.b_waitingTime = main.getServer().createBossBar("§aAguarde §2" + leftTimeToStart + " segundos §apara começar", BarColor.GREEN, BarStyle.SEGMENTED_10);
		this.b_leftTime = main.getServer().createBossBar("", BarColor.GREEN, BarStyle.SEGMENTED_6);
	}
	
	private void updateBossBars() {
		this.b_waitingPlayers.setTitle("§9Aguarde §b" + (minPlayersToStart - waitingPlayers.size()) + " jogadores §9para iniciar o cronometro");
		this.b_waitingTime.setTitle("§aAguarde §2" + leftTimeToStart + " segundos §apara começar");
		this.b_leftTime.setTitle(getMatchLeftTime());
		this.b_waitingTime.setProgress(Math.abs(((double) leftTimeToStart) / (double) timeToStart));
	}
	
	public String getMatchLeftTime() {
		int minutes = 0;
		int seconds = matchLeftTime;
		while(seconds >= 60) {
			minutes++;
			seconds -= 60;
		}
		String color;
		      if(matchLeftTime < 45)  color = "§4";
		 else if(matchLeftTime < 90)  color = "§c";
		 else if(matchLeftTime < 240) color = "§6";
		 else 						  color = "§a";
		return color + (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
	}
	
	public boolean joinPlayer(Player player) {
		if(canJoinPlayers()) {
			if(this.manager.playerIsInMatch(player)) {
				return false;
			}
			PlayerJoinMatchEvent event = new PlayerJoinMatchEvent(this, player);
			if(event.isCancelled()) {
				if(event.getCancelledReason() != null) {
					player.sendMessage(event.getCancelledReason());
				}
				return false;
			}
			this.waitingPlayers.add(player);
			player.getInventory().clear();
			PlayerUtils.clearPotionEffects(player);
			PlayerUtils.cleanPlayer(player);
			player.teleport(map.getSpawnLocation());
			if(status == MatchStatus.WAITING) {
				this.b_waitingPlayers.addPlayer(player);
			} else {
				this.b_waitingTime.addPlayer(player);
			}
			player.sendMessage(" ");
			player.sendMessage("§a» §7Utilize §f/kbon §7ou §f/kboff §7para votar a respeito do knockback da partida.");
			player.sendMessage("§a» §7Utilize §f/dia §7ou §f/noite §7para votar a respeito do horário da partida.");
			player.sendMessage(" ");
			TextComponent text = new TextComponent("§9» §7Conectado com êxito. (Mapa: " + this.getMapName() + ")");
			text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Partida do ID §6" + id)));
			player.spigot().sendMessage(text);
			player.sendMessage(" ");
			player.setPlayerListName(player.getName());
			this.updatePlayer(player);
			player.setScoreboard(voteScoreboard.getScoreboard());
			SignJoin.updateSigns();
			if(isFull() && ConfigManager.instance.autoStartMatch()) {
				boolean hasMatch = false;
				for(Match match : this.manager.getMatchs()) {
					if(match.canJoinPlayers()) {
						hasMatch = true;
						break;
					}
				}
				if(!hasMatch) {
					this.manager.addMatch();
				}
			}
			this.fixInvisible(player);
			if(settings.containsShop()) {
				player.getInventory().setItem(8, shopItem);
			}
			return true;
		}
		return false;
	}
	
	private void fixInvisible(Player player) {
		for(Player player2 : waitingPlayers) {
			player2.showPlayer(Main.getPlugin(), player);
			player.showPlayer(Main.getPlugin(), player2);
		}
	}
	
	public boolean isFull() {
		return waitingPlayers.size() >= maxPlayers || players.size() >= maxPlayers;
	}
	
	public boolean canJoinPlayers() {
		return !isFull() && waitingOrStarting();
	}
	
	public boolean waitingOrStarting() {
		return status == MatchStatus.WAITING || status == MatchStatus.STARTING;
	}
	
	public void disconnectPlayer(Player player) {
		if(hasPlayer(player)) {
			new PlayerQuitMatchEvent(this, player).callEvent();
			player.getInventory().clear();
			PlayerUtils.clearPotionEffects(player);
			PlayerUtils.cleanPlayer(player);
			this.getCurrentBossBar().removePlayer(player);
			if(!waitingOrStarting()) {
				Capturer casted = this.getCasted(player);
				if(casted.isBeingCarrying()) {
					casted.whoIsCarrying().release(casted);
				} else {
					casted.releaseAll();
				}
				casted.unregister();
				HandlerList.unregisterAll(casted.getRadar());
				for(Capturer c : players) {
					c.getSkorbord().playerDisconnected(casted);
				}
			}
			this.restorePlayer(player);
			(waitingOrStarting() ? waitingPlayers : players).remove(player);
			player.teleport(ConfigManager.instance.getLobbyLocation());
			player.setPlayerListName(player.getName());
			SignJoin.updateSigns();
			if(knockbackDisabledVoted.contains(player)) {
				knockbackDisabledVoted.remove(player);
				this.votes.addKnockbackDisabledVotes(-1);
			}
			if(knockbackEnabledVoted.contains(player)) {
				knockbackEnabledVoted.remove(player);
				this.votes.addKnockbackEnabledVotes(-1);
			}
			if(timeDayVoted.contains(player)) {
				timeDayVoted.remove(player);
				this.votes.addDayVotes(-1);
			}
			if(timeNightVoted.contains(player)) {
				timeNightVoted.remove(player);
				this.votes.addNightVotes(-1);
			}
		}
	}
	
	public boolean hasPlayer(Player player) {
		return (waitingOrStarting() ? waitingPlayers : players).contains(player);
	}
	
	@Deprecated
	public boolean has(Entity entity) {
		if(entity instanceof Player) {
			return this.hasPlayer((Player) entity);
		}
		return entity.getWorld().getName().equals(map.getMapName());
	}
	
	public void init() {
		new MatchPreStartedEvent(this).callEvent();
		this.cleanWorld();
		this.map.getWorld().setDifficulty(settings.getDifficulty());
		this.preStartControl = new BukkitRunnable() {	
			@Override
			public void run() {
				updateBossBars();
				if(waitingPlayers.size() >= minPlayersToStart) {
					if(status == MatchStatus.WAITING) {
						status = MatchStatus.STARTING;
						leftTimeToStart = timeToStart;
						b_waitingPlayers.removeAll();
						waitingPlayers.forEach(player -> b_waitingTime.addPlayer(player));
					}
				} else {
					if(status == MatchStatus.STARTING) {
						status = MatchStatus.WAITING;
						leftTimeToStart = timeToStart;
						b_waitingTime.removeAll();
						waitingPlayers.forEach(player -> b_waitingPlayers.addPlayer(player));
					}
				}
				if(leftTimeToStart < 0 && status == MatchStatus.STARTING) {
					start();
					return;
				}
			}
		}.runTaskTimer(main, 1l, 1l);
		this.preStartTime = new BukkitRunnable() {
			@Override
			public void run() {
				if(status == MatchStatus.STARTING) {
					--leftTimeToStart;
					if(leftTimeToStart <= 3 && leftTimeToStart >= 0) {
						waitingPlayers.forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f));
					}
				}
			}
		}.runTaskTimer(main, 20l, settings.getTasksTimeDelay());
		this.voteScoreboard = new VoteScoreboard(this);
		this.waitingPlayers.forEach(player -> updatePlayer(player));
		this.waitingPlayers.forEach(player -> player.setScoreboard(voteScoreboard.getScoreboard()));
		this.votes = new Votes();
		this.knockbackEnabledVoted = new HashSet<>();
		this.knockbackDisabledVoted = new HashSet<>();
		this.timeDayVoted = new HashSet<>();
		this.timeNightVoted = new HashSet<>();
	}
	
	public void start() {
		this.status = MatchStatus.RUNNING;
		new MatchStartedEvent(this).callEvent();
		this.preStartControl.cancel();
		this.preStartControl = null;
		this.preStartTime.cancel();
		this.preStartTime = null;
		this.b_waitingTime.removeAll();
		this.settings.setGenericKnockbackResistence(votes.getKnockbackWinner() == KnockbackVote.DISABLED ? 100.0d : 0.0d);
		if(votes.getTimeWinner() != TimeVote.TIE) {
			this.settings.setTimeOfWorld(votes.getTimeWinner() == TimeVote.DAY ? 5000l : 999999l);
			this.settings.setDoDaylightCycle(false);
		}
		Map<Player, Team> teams = new HashMap<>();
		List<Player> list = new ArrayList<>(waitingPlayers);
		int initialSize = list.size();
		Random r = new Random();
		for(int i = 0; i < initialSize; ++i) {
			int drawn = r.nextInt(list.size());
			Player player = list.get(drawn);
			teams.put(player, i % 2 == 0 ? Team.BLUE : Team.RED);
			list.remove(player);
		}
		for(Player player : waitingPlayers) {
			Capturer capturer = Capturer.cast(this, player).build(teams.get(player));
			capturer.teleportToBase();
			this.b_leftTime.addPlayer(capturer);
			this.players.add(capturer);
			player.setGameMode(GameMode.SURVIVAL);
			player.setMaximumNoDamageTicks(14);
			this.updatePlayer(player);
		}
		teams.clear();
		this.players.forEach(player -> player.setScoreboard(new Skorbord(player)));
		this.waitingPlayers.clear();
		this.moveReleased = true;
		this.matchControl = new BukkitRunnable() {
			@Override
			public void run() {
				if(matchLeftTime < 0) {
					Match.this.finalize(null, true);
					return;
				}
				if(settings.noPlayersToLose()) {
					if(getPlayersAlive(Team.RED).isEmpty()) {
						Match.this.finalize(Team.BLUE, false);
					} else if(getPlayersAlive(Team.BLUE).isEmpty()) {
						Match.this.finalize(Team.RED, false);
					}
				}
			}
		}.runTaskTimer(main, 1l, 1l);
		this.matchTime = new BukkitRunnable() {
			@Override
			public void run() {
				--matchLeftTime;
				updateBossBars();
			}
		}.runTaskTimer(main, 20l, settings.getTasksTimeDelay());
		this.updateWorld();
		SignJoin.updateSigns();
		if(ConfigManager.instance.autoStartMatch()) {
			boolean hasMatch = false;
			for(Match match : this.manager.getMatchs()) {
				if(match.canJoinPlayers()) {
					hasMatch = true;
					break;
				}
			}
			if(!hasMatch) {
				this.manager.addMatch();
			}
		}
	}
	
	public void finalize(Team winner, boolean timeFinished) {
		if(timeFinished) {
			new MatchFinishedTimeEvent(this).callEvent();
		} else {
			winner = ((MatchFinalizedEvent) new MatchFinalizedEvent(this, winner).callEvent()).getWinner();
			this.winner = winner;
		}
		if(waitingOrStarting()) {
			this.shutdown();
			return;
		} else {
			if(matchControl != null) {
				this.matchControl.cancel();
				this.matchControl = null;
			}
			if(matchTime != null) {
				this.matchTime.cancel();
				this.matchTime = null;
			}
			for(Capturer player : players) {
				ConfigManager.instance.addMatch(player.getName());
				PlayerUtils.clearPotionEffects(player);
				this.b_leftTime.removePlayer(player);
				if(player.getTeam() == winner) {
					ConfigManager.instance.addWin(player.getName());
					ConfigManager.instance.depositBalance(player.getName(), ConfigManager.instance.getAwardBalance());
				}
				if(player.isCarrying()) {
					player.releaseAll();
				}
				if(player.isCaptured()) {
					player.wasRescued();
				}
				HandlerList.unregisterAll(player.getRadar());
				ConfigManager.instance.addCaptured(player.getName(), player.getPlayersCaptured().size());
				ConfigManager.instance.addSaved(player.getName(), player.getPlayersRescued().size());
				for(int i = 0; i < player.getCapturedBy().size(); ++i) ConfigManager.instance.addBeenCaptured(player.getName());
			}
		}
		ConfigManager.instance.addTotalMatch();
		this.status = MatchStatus.ENDING;
		if(settings.containsHighlight()) {
			Set<Capturer> highlights = this.getCurrentHighlights();
			if(highlights.isEmpty()) {
				this.preShutdown(80l);
			} else {
				if(highlights.size() > 1 && !settings.isSplitHighlight()) {
					this.preShutdown(80l);
				} else {
					new BukkitRunnable() {
						@Override
						public void run() {
							int index = 0;
							StringBuilder sb = new StringBuilder();
							for(Capturer highlight : highlights) {
								if(index == 0) {
									sb.append(highlight.getTeam().getColorPreffix() + highlight.getName());
								} else if(index == (highlights.size() - 1)) {
									sb.append(" §de " + highlight.getTeam().getColorPreffix() + highlight.getName());
								} else {
									sb.append("§d, " + highlight.getTeam().getColorPreffix() + highlight.getName());
								}
								ConfigManager.instance.depositBalance(highlight.getName(), settings.getHighlightAward());
								highlight.sendMessage("§2Recompensa por destaque: $" + Formats.decimalFormat(settings.getHighlightAward()) + " coin" + (settings.getHighlightAward() > 1 ? "s" : "") + ".");
								++index;
							}
							sendMessageToPlayers("§5» §dO" + (highlights.size() > 1 ? "s" : "") + " jogador" + (highlights.size() > 1 ? "es" : "") + sb.toString() + " §dfo" + (highlights.size() > 1 ? "ram" : "i") + " quem mais se destac" + (highlights.size() > 1 ? "aram" : "ou") + " na partida!");
							preShutdown(110l);
						}
					}.runTaskLater(Main.getPlugin(), 75l);
				}
			}
		} else {
			this.preShutdown(80l);
		}
	}
	
	private void preShutdown(long delay) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Match.this.shutdown();
			}
		}.runTaskLater(main, delay);
	}
	
	public void shutdown() {
		new MatchShutdownEvent(this).callEvent();
		if(waitingOrStarting()) {
			Channel.sendMessageToPlayers(id, "§cSua partida foi cancelada.");
		}
		(waitingOrStarting() ? waitingPlayers : players).forEach(player -> {
			PlayerUtils.cleanPlayer(player);
			PlayerUtils.clearPotionEffects(player);
			player.getInventory().clear();
			if(!ConfigManager.instance.autoJoinMatch()) {
				player.teleport(ConfigManager.instance.getLobbyLocation());
			}
			this.restorePlayer(player);
			if(player instanceof Capturer) {
				((Capturer) player).unregister();
			}
			player.setPlayerListName(player.getName());
		});
		this.getCurrentBossBar().removeAll();
		MapTheme.makeMapAvailable(getMapName());
		this.manager.getMatchs().remove(this);
		HandlerList.unregisterAll(this);
		this.restoreWorld();
		SignJoin.updateSigns();
		if(ConfigManager.instance.autoStartMatch()) {
			boolean hasMatch = false;
			for(Match match : this.manager.getMatchs()) {
				if(match.canJoinPlayers()) {
					hasMatch = true;
					break;
				}
			}
			if(!hasMatch) {
				this.manager.addMatch();
			}
		}
		if(ConfigManager.instance.autoJoinMatch()) {
			for(Capturer player : players) {
				boolean joined = false;
				for(Match match : this.manager.getMatchs()) {
					if(match.canJoinPlayers()) {
						player.sendMessage("§aConectando em §eCapture " + (match.getId() + 1) + "§a...");
						joined = match.joinPlayer(player);
						break;
					}
				}
				if(!joined) {
					player.teleport(ConfigManager.instance.getLobbyLocation());
					player.sendMessage("§cNenhuma partida disponível encontrada.");
				}
			}
		}
	}
	
	public boolean sameTeam(Capturer... players) {
		if(players.length == 0) {
			return false;
		}
		Team team = players[0].getTeam();
		for(Capturer player : players) {
			if(player.getTeam() != team) {
				return false;
			}
		}
		return true;
	}
	
	@EventHandler
	public void onDisconnect(PlayerQuitEvent e) {
		if(hasPlayer(e.getPlayer())) {
			this.disconnectPlayer(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onReleaseInBase(PlayerMoveEvent e) {
		if(hasPlayer(e.getPlayer()) && !waitingOrStarting() && moveReleased) {
			Capturer player = this.getCasted(e.getPlayer());
			if(!player.isCaptured() && player.isCarrying()) {
				RectLocation base = map.getRectLocation(player.getTeam().getTeamBaseName());
				if(base.isHovered(player)) {
					ArrayList<Capturer> carrying = new ArrayList<>(player.getCarrying());
					player.releaseAll();
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.5f);
					new BukkitRunnable() {
						@Override
						public void run() {
							for(Capturer carryied : carrying) {
								if(carryied.getTeam() == player.getTeam()) {
									carryied.wasRescued();
									carryied.getRescuedBy().add(player);
									carryied.teleport(map.getRectLocation(carryied.getTeam().getTeamBaseName()).getCenterLocation());
									player.getPlayersRescued().add(carryied);
								} else {
									carryied.wasCaptured();
									carryied.getCapturedBy().add(player);
									player.getPlayersCaptured().add(carryied);
								}
								sendMessageToPlayers(carryied.getTeam().getColorPreffix() + carryied.getName() + " §6foi " + (carryied.getTeam() == player.getTeam() ? "salvo" : "capturado") + " por " + player.getTeam().getColorPreffix() + player.getName() + "§6!");
							}
						}
					}.runTaskLater(main, 1l);
				}
			}
		}
	}
	
	@EventHandler
	public void onCapturePlayer(EntityDamageByEntityEvent e) {
		if(!(e.getEntity() instanceof Player)) {
			e.setCancelled(settings.isInvincibleEntities());
			return;
		}
		Player player = (Player) e.getEntity();
		if(e.getDamager() instanceof Player) {
			Player capturer = (Player) e.getDamager();
			if(hasPlayer(player) && hasPlayer(capturer)) {
				if(waitingOrStarting() || status == MatchStatus.ENDING) {
					e.setCancelled(true);
				} else {
					if(this.sameTeam(getCasted(player), getCasted(capturer))) {
					e.setCancelled(true);
						return;
					}
					if((player.getHealth() - e.getDamage() <= 0) && !getCasted(player).isBeingCarrying()) {
						e.setCancelled(true);
						player.setHealth(20.0d);
						this.getCasted(capturer).carry(getCasted(player));
						player.sendMessage("§cVocê está sendo capturado por " + capturer.getName() + ".");
						capturer.sendMessage("§aVocê está capturando §e" + player.getName() + "§a.");
					}
				}
			}
		} else if(e.getDamager() instanceof Arrow) {
			Arrow a = (Arrow) e.getDamager();
			if(a.getShooter() instanceof Player) {
				Player capturer = (Player) a.getShooter();
				if(hasPlayer(player) && hasPlayer(capturer)) {
					if(waitingOrStarting() || status == MatchStatus.ENDING) {
						e.setCancelled(true);
					} else {
						if(this.sameTeam(getCasted(player), getCasted(capturer))) {
							e.setCancelled(true);
							return;
						}
						if(settings.headshotsEnabled() && (Math.abs(a.getLocation().getY() - player.getLocation().getY()) > settings.getYDiffHeadshot())) {
							double original = e.getDamage();
							double more = ((e.getDamage() * settings.getHeadshotPercentMoreDamage()) / 100.0d);
							double damage = e.getDamage() + more;
							e.setDamage(damage);
							capturer.sendMessage("§aHeadshot em §e" + player.getName() + ": §c- " + Formats.decimalFormat(damage) + "HP (dano original: " + Formats.decimalFormat(original) + " HP)");
						}
						if(player.getHealth() - e.getDamage() <= 0) {
							e.setCancelled(true);
							player.setHealth(20.0d);
							this.getCasted(capturer).carry(getCasted(player));
							player.sendMessage("§cVocê está sendo capturado por " + capturer.getName() + ".");
							capturer.sendMessage("§aVocê está capturando §e" + player.getName() + "§a.");
						}
					}
				}
			} else {
				e.setCancelled(true);
			}
		} else {
			if(settings.isVulnerableEntities()) {
				if((player.getHealth() - e.getDamage() <= 0) || waitingOrStarting()) {
					e.setCancelled(true);
				}
			} else {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onRescuePlayer(PlayerInteractEntityEvent e) {
		if(!(e.getRightClicked() instanceof Player)) {
			return;
		}
		Capturer player = getCasted(e.getPlayer());
		Capturer rescued = getCasted((Player) e.getRightClicked());
		if(this.manager.sameMatch(player, rescued) && rescued.isCaptured() && sameTeam(player, rescued) && !rescued.isBeingCarrying()) {
			if(e.getHand() == EquipmentSlot.HAND && !rescued.isBeingCarrying() && !player.isCaptured() && !player.isBeingCarrying()) {
				player.carry(rescued);
				player.sendMessage("§aVocê está salvando §b" + rescued.getName() + "§a.");
				rescued.sendMessage("§2Você está sendo salvo por " + player.getName() + ".");
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if(e.getEntity().getWorld() == map.getWorld()) {
			if(settings.isInvincibleEntities()) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			if(hasPlayer(player) && waitingOrStarting()) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBreakBlock(BlockBreakEvent e) {
		if(e.getPlayer().isOp() && e.getPlayer().getGameMode() == GameMode.CREATIVE) {
			return;
		}
		if(e.getBlock().getWorld() == map.getWorld()) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent e) {
		if(e.getBlock().getWorld() == map.getWorld()) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent e) {
		if(hasPlayer(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPickupItem(PlayerPickupItemEvent e) {
		if(hasPlayer(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPickupArrow(PlayerPickupArrowEvent e) {
		if(hasPlayer(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onItemBreak(PlayerItemDamageEvent e) {
		if(hasPlayer(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onRemoveArmor(InventoryClickEvent e) {
		if(!(e.getWhoClicked() instanceof Player)) {
			return;
		}
		if(hasPlayer((Player) e.getWhoClicked())) {
			if(e.getSlotType() == SlotType.ARMOR) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		if(!(e.getEntity() instanceof Player)) {
			return;
		}
		if(hasPlayer((Player) e.getEntity())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onWorldWeather(WeatherChangeEvent e) {
		if(!settings.canRains()) {
			e.getWorld().setWeatherDuration(0);
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onSpawnEntity(EntitySpawnEvent e) {
		e.setCancelled(has(e.getEntity()) && settings.canSpawnEntities());
	}
	
	public void restorePlayers() {
		if(waitingOrStarting()) {
			for(Player player : waitingPlayers) {
				player.teleport(ConfigManager.instance.getLobbyLocation());
				PlayerUtils.cleanPlayer(player);
				PlayerUtils.clearPotionEffects(player);
				PlayerUtils.clearInventory(player);
				player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			}
		} else {
			for(Capturer player : players) {
				player.releaseAll();
				player.wasRescued();
				player.teleport(ConfigManager.instance.getLobbyLocation());
				PlayerUtils.cleanPlayer(player);
				PlayerUtils.clearPotionEffects(player);
				PlayerUtils.clearInventory(player);
				player.setPlayerListName(player.getName());
				player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			}
		}
		this.getCurrentBossBar().removeAll();
	}
	
	public int getLeftTimeToStart() {
		return leftTimeToStart;
	}

	public void setLeftTimeToStart(int leftTimeToStart) {
		this.leftTimeToStart = leftTimeToStart;
	}

	public MatchStatus getStatus() {
		return status;
	}

	public int getId() {
		return id;
	}

	public MapTheme getMapTheme() {
		return map;
	}
	
	public String getMapName() {
		return map.getMapName();
	}
	
	public World getMapWorld() {
		return map.getWorld();
	}
	
	public int getMaxPlayers() {
		return maxPlayers;
	}

	public Set<Player> getWaitingPlayers() {
		return waitingPlayers;
	}

	public Set<Capturer> getPlayers() {
		return players;
	}
	
	public Capturer getCasted(Player player) {
		for(Capturer c : players) {
			if(c.equals(player)) {
				return c;
			}
		}
		return null;
	}
	
	public Set<Capturer> getPlayers(Team team) {
		return new HashSet<>(this.players.stream().filter(player -> player.getTeam() == team).collect(Collectors.toList()));
	}
	
	public Set<Player> getJustPlayers() {
		if(waitingOrStarting()) {
			return waitingPlayers;
		}
		Set<Player> players = new HashSet<>();
		this.players.forEach(player -> players.add(player));
		return players;
	}
	
	public Set<Capturer> getPlayersAlive(Team team) {
		return new HashSet<>(this.players.stream().filter(player -> player.getTeam() == team && !player.isCaptured()).collect(Collectors.toList()));
	}
	
	public int getMinPlayersToStart() {
		return minPlayersToStart;
	}

	public int getTimeToStart() {
		return timeToStart;
	}
	
	public BossBar getCurrentBossBar() {
		switch(status) {
			case WAITING:
				return b_waitingPlayers;
			case STARTING:
				return b_waitingTime;
			default:
				return b_leftTime;
		}
	}
	
	public Settings getSettings() {
		return settings;
	}
	
	public Settings setSettings(Settings settings) {
		this.settings = settings;
		return this.settings;
	}
	
	public void sendMessageToPlayers(Object message) {
		Channel.sendMessageToPlayers(id, message);
	}
	
	public void sendMessageToTeam(Team team, Object message) {
		Channel.sendMessageToTeam(id, team, message);
	}
	
	public void updatePlayer(Player player) {
		player.setWalkSpeed(settings.getPlayersWalkSpeed());
		player.setFlySpeed(settings.getPlayersFlySpeed());
		player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(settings.getGenericAttackSpeed());
		player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(settings.getGenericKnockbackResistence());
	}
	
	public void restorePlayer(Player player) {
		player.setWalkSpeed(PlayerUtils.DEFAULT_WALK_SPEED);
		player.setFlySpeed(PlayerUtils.DEFAULT_FLY_SPEED);
		player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(PlayerUtils.DEFAULT_VALUE_ATTACK_SPEED);
		player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(PlayerUtils.DEFAULT_VALUE_KNOCKBACK_RESISTANCE);
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}
	
	public void updateWorld() {
		this.map.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, settings.doDaylightCycle());
		if(!settings.doDaylightCycle()) {
			this.map.getWorld().setTime(settings.getTimeOfWorld());	
		}
	}
	
	public void restoreWorld() {
		this.map.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
	}
	
	public Team enemyTeam(Capturer capturer) {
		if(capturer.getTeam() == Team.BLUE) {
			return Team.RED;
		} else {
			return Team.BLUE;
		}
	}
	
	public ItemStack getShopItem() {
		return shopItem;
	}
	
	public Team getWinner() {
		return winner;
	}
	
	@EventHandler
	public void onUseShopItem(PlayerInteractEvent e) {
		if(e.getItem() == null) {
			return;
		}
		if(e.getItem().equals(shopItem) && hasPlayer(e.getPlayer()) && status != MatchStatus.ENDING) {
			if(e.getAction() == Action.RIGHT_CLICK_AIR || (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getHand() == EquipmentSlot.HAND)) {
				e.setCancelled(true);
				ShopInventory.openInventory(e.getPlayer());
			}
		}
	}
	
	public Set<Capturer> getCurrentHighlights() {
		Set<Capturer> highlights = new HashSet<>();
		if(players.isEmpty()) {
			return highlights;
		}
		Capturer highlight = null;
		for(Capturer player : players) {
			if(highlight == null) {
				highlight = player;
				continue;
			}
			if(player.getPlayersCaptured().size() > highlight.getPlayersCaptured().size()) {
				highlight = player;
			}
		}
		if(highlight.getPlayersCaptured().size() < 3) {
			return highlights;
		}
		for(Capturer player : players) {
			if(player.getPlayersCaptured().size() >= highlight.getPlayersCaptured().size()) {
				highlights.add(player);
			}
		}
		return highlights;
	}
	
	public void updateVotes() {
		this.voteScoreboard.updateVotes();
	}
	
	public Votes getVotes() {
		return votes;
	}
	
	public Set<Player> getKnockbackEnabledVoteds() {
		return knockbackEnabledVoted;
	}
	
	public Set<Player> getKnockbackDisabledVoteds() {
		return knockbackDisabledVoted;
	}
	
	public Set<Player> getTimeDayVoteds() {
		return timeDayVoted;
	}
	
	public Set<Player> getTimeNightVoteds() {
		return timeNightVoted;
	}
	
	private void cleanWorld() {
		this.map.getWorld().getLivingEntities().forEach(entity -> entity.remove());
	}
	
	public void playSound(Sound sound) {
		(waitingOrStarting() ? waitingPlayers : players).forEach(player -> playSound(player, sound));
	}
	
	public void playSound(Player player, Sound sound) {
		if(settings.containsSounds()) {
			player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
		}
	}
	
}