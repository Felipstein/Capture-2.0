package br.felipe.capture.game;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import br.felipe.capture.Main;
import br.felipe.capture.config.ConfigManager;
import br.felipe.capture.events.MatchFinalizedEvent;
import br.felipe.capture.events.MatchFinishedTimeEvent;
import br.felipe.capture.events.MatchStartedEvent;
import br.felipe.capture.events.PlayerBoughtItemEvent;
import br.felipe.capture.events.PlayerQuitMatchEvent;
import br.felipe.capture.game.Votes.KnockbackVote;
import br.felipe.capture.game.Votes.TimeVote;
import br.felipe.capture.game.profiles.Team;
import br.felipe.capture.utils.ListUtils;

public class LoggerListener implements Listener {
	
	@EventHandler
	public void onMatchStart(MatchStartedEvent e) {
		Main.getPlugin().getLogger().info("Partida " + e.getMatch().getId() + " em andamento.");
		if(!e.getMatch().getSettings().messagesEnabled()) {
			return; 
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				e.getMatch().sendMessageToPlayers(" ");
				KnockbackVote knockbackWinner = e.getMatch().getVotes().getKnockbackWinner();
				if(knockbackWinner == KnockbackVote.TIE) {
					e.getMatch().sendMessageToPlayers("§6» §7Devido ao empate da votação sobre o knockback, continuará ativo.");
				} else if(knockbackWinner == KnockbackVote.ENABLED) {
					e.getMatch().sendMessageToPlayers("§a» §7Knockback está §aativo§7!");
				} else {
					e.getMatch().sendMessageToPlayers("§c» §7Knockback está §cdesativado§7!");
				}
				TimeVote timeWinner = e.getMatch().getVotes().getTimeWinner();
				if(timeWinner == TimeVote.TIE) {
					e.getMatch().sendMessageToPlayers("§6» §7Devido ao empate da votação sobre o horário, continuará do jeito que está.");
				} else if(timeWinner == TimeVote.DAY) {
					e.getMatch().sendMessageToPlayers("§a» §7O céu está de §edia§7!");
				} else {
					e.getMatch().sendMessageToPlayers("§c» §7O céu está de §9noite§7!");
				}
			}
		}.runTaskLater(Main.getPlugin(), 7l);
		new BukkitRunnable() {
			@Override
			public void run() {
				e.getMatch().sendMessageToPlayers(" ");
				e.getMatch().sendMessageToPlayers("§6» §b" + ListUtils.getRandomElement("Ora ora ora", "Muda muda muda", "Kono Dio Da", "Wryyyyyyyyyy", "Za Warudo", "Yare yare daze!"));
				e.getMatch().sendMessageToPlayers(" ");
			}
		}.runTaskLater(Main.getPlugin(), 16l);
	}
	
	@EventHandler
	public void onMatchFinalize(MatchFinalizedEvent e) {
		Main.getPlugin().getLogger().info("Partida " + e.getMatch().getId() + " finalizada.");
		if(!e.getMatch().getSettings().messagesEnabled()) {
			return;
		}
		e.getMatch().sendMessageToPlayers(" ");
		if(e.getWinner() == null) {
			e.getMatch().sendMessageToPlayers("§6    Empate!");
			e.getMatch().sendMessageToPlayers("§c " + ListUtils.getRandomElement("Ninguém ganhou ou perdeu, nem ganhou ou perdeu, ou ganhou ou perdeu, todo mundo perdeu.", "Ambos os times não venceram essa partida."));
		} else {
			if(e.getWinner() == Team.BLUE) {
				e.getMatch().sendMessageToPlayers("§6    Vitória da equipe §9Azul§6!");
				e.getMatch().sendMessageToTeam(Team.RED, "§c Todos os jogadores da sua equipe foram capturados.");
			} else {
				e.getMatch().sendMessageToPlayers("§6    Vitória da equipe §cVermelha§6!");
				e.getMatch().sendMessageToTeam(Team.BLUE, "§c Todos os jogadores da sua equipe foram capturados.");
			}
			e.getMatch().sendMessageToTeam(e.getWinner(), "§e Todos os jogadores da equipe adversária foram capturados.");
			e.getMatch().sendMessageToTeam(e.getWinner(), "§2Recompensa: $" + ConfigManager.instance.getAwardBalance() + " coins");
		}
		e.getMatch().sendMessageToPlayers(" ");
	}
	
	@EventHandler
	public void onMatchFinalize2(MatchFinishedTimeEvent e) {
		Main.getPlugin().getLogger().info("Partida " + e.getMatch().getId() + " finalizada (tempo).");
		if(!e.getMatch().getSettings().messagesEnabled()) {
			return;
		}
		e.getMatch().sendMessageToPlayers(" ");
		e.getMatch().sendMessageToPlayers("§6    Tempo finalizado!");
		e.getMatch().sendMessageToPlayers("§c Nenhum time foi apto em capturar a equipe adversária a tempo.");
		e.getMatch().sendMessageToPlayers(" ");
	}
	
	@EventHandler
	public void onPlayerQuitMatch(PlayerQuitMatchEvent e) {
		e.getMatch().sendMessageToPlayers("§cJogador " + e.getPlayer().getName() + " §cdesconectou da partida.");
	}
	
	@EventHandler
	public void onPlayerBoughtItem(PlayerBoughtItemEvent e) {
		e.getMatch().sendMessageToPlayers("§6» §bJogador §6" + e.getPlayer().getName() + " §bcomprou um item na loja por §6" + e.getItem().getPrice() + " coin" + (e.getItem().getPrice() > 1 ? "s" : "") + "§b.");
		e.getPlayer().sendMessage("§a» §aVocê gastou " + e.getItem().getPrice() + " coin" + (e.getItem().getPrice() > 1 ? "s" : "") + " nesse item!");
	}
	
}