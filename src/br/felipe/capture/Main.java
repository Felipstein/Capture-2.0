package br.felipe.capture;

import java.io.File;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;

import br.felipe.capture.channels.Channel;
import br.felipe.capture.command.CommandsManager;
import br.felipe.capture.command.commands.*;
import br.felipe.capture.command.commands.channels.*;
import br.felipe.capture.command.commands.votes.*;
import br.felipe.capture.config.ConfigManager;
import br.felipe.capture.game.LoggerListener;
import br.felipe.capture.game.MatchsManager;
import br.felipe.capture.locations.MapTheme;
import br.felipe.capture.signs.SignJoin;

public class Main extends JavaPlugin implements Listener {
	
	private static Main main;
	
	public void onEnable() {
		main = this;
		ConfigManager.instance = new ConfigManager(this, new File(this.getDataFolder(), "config.yml"), this.getConfig());
		MatchsManager.instance = new MatchsManager(this);
		MapTheme.loadMaps();
	//	new OpUnavailable();
		this.loadCommands();
		this.registerEvents(
			this,
			new LoggerListener()
		);
		SignJoin.loadSigns();
		if(ConfigManager.instance.autoStartMatch()) {
			MatchsManager.instance.addMatch();
		}
	}
	
	@Override
	public void onDisable() {
		MatchsManager.instance.getMatchs().forEach(match -> match.restorePlayers());
	}
	
	private void registerEvents(Listener... listeners) {
		for(Listener listener : listeners) {
			this.getServer().getPluginManager().registerEvents(listener, this);
		}
	}
	
	private void loadCommands() {
		CommandsManager.putCommand(new AuthCommand());
		CommandsManager.putCommand(new BalanceCommand());
		CommandsManager.putCommand(new CaptureCommand());
		CommandsManager.putCommand(new MapsCommand());
		CommandsManager.putCommand(new ProfileCommand());
		CommandsManager.putCommand(new RankingCommand());
		CommandsManager.putCommand(new ShopCommand());
		CommandsManager.putCommand(new SpawnCommand());
		CommandsManager.putCommand(new WinsCommand());
		CommandsManager.putCommand(new GlobalCommand());
		CommandsManager.putCommand(new LocalCommand());
		CommandsManager.putCommand(new ResponseCommand());
		CommandsManager.putCommand(new TeamCommand());
		CommandsManager.putCommand(new TellCommand());
		CommandsManager.putCommand(new DayCommand());
		CommandsManager.putCommand(new KnockbackDisabledCommand());
		CommandsManager.putCommand(new KnockbackEnabledCommand());
		CommandsManager.putCommand(new NightCommand());
	}
	
	@EventHandler
	public void onPreJoin(AsyncPlayerPreLoginEvent e) {
		if(Bukkit.getPlayer(e.getName()) != null) {
			e.disallow(Result.KICK_OTHER, "§cSacanagem, né?");
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if(!ConfigManager.instance.hasProfile(e.getPlayer().getName())) {
			ConfigManager.instance.createProfile(e.getPlayer().getName());
		}
		if(ConfigManager.instance.hasLobbyLocationSetted()) {
			e.getPlayer().teleport(ConfigManager.instance.getLobbyLocation());
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if(Channel.response.containsKey(e.getPlayer())) {
			Channel.response.remove(e.getPlayer());
		}
		if(Channel.response.containsValue(e.getPlayer())) {
			Set<Player> keys = new HashSet<>();
			for(Entry<CommandSender, CommandSender> entry : Channel.response.entrySet()) {
				Player key = (Player) entry.getKey();
				Player value = (Player) entry.getValue();
				if(value == e.getPlayer()) {
					keys.add(key);
				}
			}
			keys.forEach(key -> Channel.response.remove(key));
		}
	}
	
	@EventHandler
	public void onSendMessage(AsyncPlayerChatEvent e) {
		e.setCancelled(true);
		Channel.sendLocalMessage(e.getPlayer(), e.getMessage().replace('&', '§'));
	}
	
	@EventHandler
	public void a(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if(!MatchsManager.instance.playerIsInMatch(p)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void b(BlockBreakEvent e) {
		if(!e.getPlayer().isOp()) {
			e.setCancelled(true);
		} else {
			if(SignJoin.hasSignJoin(e.getBlock().getLocation()) && e.getPlayer().getGameMode() == GameMode.CREATIVE) {
				SignJoin sign = SignJoin.getSignJoin(e.getBlock().getLocation());
				sign.remove();
				e.getPlayer().sendMessage("§cPlaca " + sign.getId() + " removida.");
			}
		}
	}
	
	@EventHandler
	public void c(BlockPlaceEvent e) {
		if(!e.getPlayer().isOp()) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void d(FoodLevelChangeEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if(!MatchsManager.instance.playerIsInMatch(p)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void e(SignChangeEvent e) {
		if((e.getLine(0).equalsIgnoreCase("[capture]") || e.getLine(0).equalsIgnoreCase("[cpt]")) && e.getPlayer().isOp()) {
			int id;
			try {
				id = Integer.valueOf(e.getLine(1));
			} catch(NumberFormatException e2) {
				e.getPlayer().sendMessage("§cId incorreto.");
				e.setCancelled(true);
				return;
			}
			if(SignJoin.hasSignJoin(id)) {
				e.getPlayer().sendMessage("§cJá existe uma placa com esse id.");
				e.setCancelled(true);
				return;
			}
			SignJoin.addSign(id, e.getBlock().getLocation());
			e.getPlayer().sendMessage("§aPlaca id §e" + id + " §acriada com êxito.");
			e.setLine(0, "Capture " + (id + 1));
			e.setLine(1, "");
			e.setLine(2, "§f§l-/-");
			e.setLine(3, "§9§lAguardando");
			SignJoin.updateSigns();
		}
	}
	
	@EventHandler
	public void f(EntityDeathEvent e) {
		e.setDroppedExp(0);
		e.getDrops().forEach(item -> item.setType(Material.AIR));
	}
	
	public static Main getPlugin() {
		return main;
	}
	
	public static void playSound(Player player, Sound sound, float volume, float pitch) {
		player.playSound(player.getLocation(), sound, volume, pitch);
	}
	
}