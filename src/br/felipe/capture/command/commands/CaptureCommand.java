package br.felipe.capture.command.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.felipe.capture.Main;
import br.felipe.capture.command.Command;
import br.felipe.capture.config.ConfigManager;
import br.felipe.capture.game.Match;
import br.felipe.capture.game.MatchsManager;
import br.felipe.capture.game.Match.MatchStatus;
import br.felipe.capture.game.profiles.Capturer;
import br.felipe.capture.locations.MapTheme;
import br.felipe.capture.locations.NamedLocation;
import br.felipe.capture.locations.RectLocation;
import br.felipe.capture.locations.profile.Builder;
import br.felipe.capture.utils.ListUtils;
import br.felipe.capture.utils.PlayerUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import static  br.felipe.capture.utils.Formats.locationToString;

public class CaptureCommand extends Command {
	
	public CaptureCommand() {
		super("capture", true);
	}
	
	@Override
	public boolean onCommand(boolean console, CommandSender s, Player p, String[] args) {
		if(!console) {
			if(args.length == 0) {
				p.sendMessage("§6» §a/cpt reload §fRecarrega o arquivo config.yml.");
				p.sendMessage("§6» §a/cpt setlobby §fSeta o lobby do servidor.");
				p.sendMessage("§6» §a/cpt setspawn §fSeta e define o mundo como mapa.");
				p.sendMessage("§6» §a/cpt setredbase <pos1|pos2> §fSeta as posições da base vermelha.");
				p.sendMessage("§6» §a/cpt setbluebase <pos1|pos2>  §fSeta as posições da base azul.");
				p.sendMessage("§6» §a/cpt mapinfo §fAnalisa informações do mapa (DEVELOPER).");
				p.sendMessage("§6» §a/cpt addparty [mapa-nome] §fAdiciona uma partida.");
				p.sendMessage("§6» §a/cpt finalize <id> §fFinaliza tal partida.");
				p.sendMessage("§6» §a/cpt partys §fLista todas partidas ocorrendo.");
				p.sendMessage("§6» §a/cpt join <id> §fConecte-se em tal partida.");
				p.sendMessage("§6» §a/cpt join <id> <jogador> §fConecta tal jogador em tal partida.");
				p.sendMessage("§6» §a/cpt quit <jogador> §fDesconecta o jogador de sua partida.");
				p.sendMessage("§6» §a/cpt forcestart <id> §fForça a inicialização de tal partida.");
				p.sendMessage("§6» §a/cpt releaseall §fSolte todos os jogadores que você está carregando.");
				return true;
			}
			Location loc = p.getLocation();
			World world = loc.getWorld();
			String name = world.getName();
			boolean isMap = ConfigManager.instance.hasMap(name);
			if(args[0].equals("reload")) {
				Main.getPlugin().reloadConfig();
				p.sendMessage("§aArquivo §econfig.yml §arecarregado com êxito.");
				return true;
			} else if(args[0].equals("setlobby")) {
				ConfigManager.instance.setLobbyLocation(loc);
				p.sendMessage("§aLobby do servidor setado em §e" + locationToString(loc));
				return true;
			} else if(args[0].equals("setspawn")) {
				if(!isMap) {
					p.sendMessage("§aO mundo §6" + name + " §afoi definido como mapa.");
					MapTheme map = ConfigManager.instance.addMap(world, loc);
					MapTheme.loadMap(map);
					MapTheme.makeMapAvailable(name);
				} else {
					ConfigManager.instance.setSpawnLocation(name, loc);
				}
				p.sendMessage("§aSpawn do mapa §6" + name + " §adefinido em §e" + locationToString(loc));
				return true;
			} else if(args[0].equals("setredbase")) {
				if(args.length == 1) {
					return this.missingArguments(s, "/cpt setredbase <pos1|pos2>");
				}
				if(!isMap) {
					p.sendMessage("§cEsse mundo não está definido como mapa.");
					return false;
				}
				if(args[1].equals("pos1") || args[1].equals("pos2")) {
					Builder builder;
					if(Builder.hasBuilder(p)) {
						builder = Builder.getBuilder(p);
					} else {
						builder = Builder.startBuild(p);
					}
					if(args[1].equals("pos1")) {
						builder.setMinLocation(loc);
						if(builder.hasMaxLocationSetted()) {
							p.sendMessage("§aCom ambas localizações setadas, utilize §2/cpt setredbase build §apara definir a base.");
						} else {
							p.sendMessage("§aAgora só resta setar a localizaçõo §2posição 2§a, sete-a com §e/cpt setredbase pos2§a!");
						}
					} else {
						builder.setMaxLocation(loc);
						if(builder.hasMinLocationSetted()) {
							p.sendMessage("§aCom ambas localizações setadas, utilize §2/cpt setredbase build §apara definir a base.");
						} else {
							p.sendMessage("§aAgora só resta setar a localização §2posição 1§a, sete-a com §e/cpt setredbase pos1§a!");
						}
					}
					return true;
				} else if(args[1].equals("build")) {
					if(!Builder.hasBuilder(p)) {
						return this.invalidArgument(s, args[1]);
					}
					Builder builder = Builder.getBuilder(p);
					if(builder.hasLocationsSetted()) {
						MapTheme map = ConfigManager.instance.getMap(name);
						if(map.hasRectLocation(MapTheme.RED_BASE)) {
							map.removeRectLocation(MapTheme.RED_BASE);
						}
						map.addRectLocation(MapTheme.MAX_PRIORITY, MapTheme.RED_BASE, MapTheme.RED_BASE_NAME, builder, true, true, true);
						builder.stop();
						p.sendMessage("§aBase vermelha setada com êxito.");
						return true;
					} else {
						return this.invalidArgument(s, args[1]);
					}
				} else {
					return this.invalidArgument(s, args[1]);
				}
			} else if(args[0].equals("setbluebase")) {
				if(args.length == 1) {
					return this.missingArguments(s, "/cpt setbluebase <pos1|pos2>");
				}
				if(!isMap) {
					p.sendMessage("§cEsse mundo não está definido como mapa.");
					return false;
				}
				if(args[1].equals("pos1") || args[1].equals("pos2")) {
					Builder builder;
					if(Builder.hasBuilder(p)) {
						builder = Builder.getBuilder(p);
					} else {
						builder = Builder.startBuild(p);
					}
					if(args[1].equals("pos1")) {
						builder.setMinLocation(loc);
						if(builder.hasMaxLocationSetted()) {
							p.sendMessage("§aCom ambas localizações setadas, utilize §2/cpt setbluebase build §apara definir a base.");
						} else {
							p.sendMessage("§aAgora só resta setar a localização §2posição 2§a, sete-a com §e/cpt setbluebase pos2§a!");
						}
					} else {
						builder.setMaxLocation(loc);
						if(builder.hasMinLocationSetted()) {
							p.sendMessage("§aCom ambas localizações setadas, utilize §2/cpt setbluebase build §apara definir a base.");
						} else {
							p.sendMessage("§aAgora só resta setar a localização §2posição 1§a, sete-a com §e/cpt setbluebase pos1§a!");
						}
					}
					return true;
				} else if(args[1].equals("build")) {
					if(!Builder.hasBuilder(p)) {
						return this.invalidArgument(s, args[1]);
					}
					Builder builder = Builder.getBuilder(p);
					if(builder.hasLocationsSetted()) {
						MapTheme map = ConfigManager.instance.getMap(name);
						if(map.hasRectLocation(MapTheme.BLUE_BASE)) {
							map.removeRectLocation(MapTheme.BLUE_BASE);
						}
						map.addRectLocation(MapTheme.MAX_PRIORITY, MapTheme.BLUE_BASE, MapTheme.BLUE_BASE_NAME, builder, true, true, true);
						builder.stop();
						p.sendMessage("§aBase azul setada com êxito.");
						return true;
					} else {
						return this.invalidArgument(s, args[1]);
					}
				} else {
					return this.invalidArgument(s, args[1]);
				}
			} else if(args[0].equals("mapinfo")) {
				if(!isMap) {
					p.sendMessage("§cEsse mundo não está definido como mapa.");
					return false;
				}
				MapTheme map = ConfigManager.instance.getMap(name);
				p.sendMessage("§aLocalizações do spawn: " + (map.getSpawnLocation() != null ? "§e" + locationToString(map.getSpawnLocation()) : "§cn�o possu�"));
				p.sendMessage("§aLocalizações nomeadas:");
				if(map.getNamedLocations().isEmpty()) {
					p.sendMessage("§c» Nenhuma localização nomeada.");
				} else {
					for(NamedLocation namedLoc : map.getNamedLocations()) {
						String color = "§6";
						if(namedLoc.isHere(p)) {
							color = "§a";
						}
						p.sendMessage(color + "» §f" + namedLoc.getCodeName() + " (" + namedLoc.getName() + ") §e" + locationToString(namedLoc.getLocation()));
					}
				}
				p.sendMessage("§aLocalizações retangulares:");
				if(map.getRectLocations().isEmpty()) {
					p.sendMessage("§c» Nenhuma localização retangular.");
				} else {
					for(RectLocation rectLoc : map.getRectLocations()) {
						String color = "§6";
						if(rectLoc.isHovered(p)) {
							color = "§a";
						}
						p.sendMessage(color + "» §f" + rectLoc.getCodeName() + " (" + rectLoc.getName() + ") §e" + locationToString(rectLoc.getMinLocation()) + " §6" + locationToString(rectLoc.getMaxLocation()) + " " + (rectLoc.isHovered(p) ? "§aESTÁ DENTRO" : " §cESTÁ FORA") + " §b§l" + map.getPriorityOfRectLocation(rectLoc.getCodeName()));
					}
				}
				return true;
			} else if(args[0].equals("addparty")) {
				MapTheme map;
				if(args.length == 1) {
					if(!MapTheme.hasMapAvailable()) {
						p.sendMessage("§cNão existe nenhum mapa disponível atualmente. Adquire informações sobre os mapas com \"/maps\".");
						return false;
					}
					map = MapTheme.drawMap();
				} else {
					if(!ConfigManager.instance.hasMap(args[1])) {
						p.sendMessage("§cEsse mundo não está definido como mapa.");
						return false;
					}
					if(!MapTheme.isAvailable(args[1])) {
						p.sendMessage("§cEsse mapa não está disponível atualmente.");
						return false;
					}
					map = ConfigManager.instance.getMap(args[1]);
				}
				Match match = MatchsManager.instance.addMatch(map);
				if(match == null) {
					p.sendMessage("§cNão foi possível inicializar a partida! Verifique o CONSOLE para informaçõees.");
					return false;
				}
				p.sendMessage("§aPartida §e" + match.getId() + " §ainiciada com êxito.");
				TextComponent text = new TextComponent("�a� Clique para conectar �");
				text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cpt join " + match.getId()));
				p.spigot().sendMessage(text);
				return true;
			} else if(args[0].equals("finalize")) {
				if(args.length == 1) {
					return this.missingArguments(s, "/cpt finalize <id>");
				}
				int id;
				try {
					id = Integer.valueOf(args[1]);
				} catch(NumberFormatException e) {
					return this.invalidNumberValue(s, args[1]);
				}
				if(!MatchsManager.instance.hasMatch(id)) {
					p.sendMessage("�cN�o existe uma partida com esse ID.");
					return false;
				}
				MatchsManager.instance.finalizeMatch(id, null, false);
				p.sendMessage("�aPartida �e" + id + " �afinalizada com �xito.");
				return true;
			} else if(args[0].equals("partys")) {
				if(MatchsManager.instance.getMatchs().isEmpty()) {
					p.sendMessage("�cNenhuma partida encontrada.");
					return false;
				}
				for(Match match : MatchsManager.instance.getMatchs()) {
					p.sendMessage("�b[" + match.getId() + "] �6" + match.getMapName() + " �7- �f" + match.getJustPlayers().size() + "/�7" + match.getMaxPlayers() + " - " + (match.waitingOrStarting() ? "�aDISPON�VEL" : "�cEM ANDAMENTO"));
				}
				return true;
			} else if(args[0].equals("join")) {
				int id;
				try {
					id = Integer.valueOf(args[1]);
				} catch(NumberFormatException e) {
					return this.invalidNumberValue(s, args[1]);
				}
				if(!MatchsManager.instance.hasMatch(id)) {
					p.sendMessage("�cN�o existe uma partida com esse ID.");
					return false;
				}
				boolean myself = true;
				Player player = p;
				if(args.length > 2) {
					if(!args[2].equalsIgnoreCase(p.getName())) {
						if(args[2].equals("@a")) {
							if(!MatchsManager.instance.getMatch(id).canJoinPlayers()) {
								p.sendMessage("�cEssa partida j� est� em andamento ou est� indispon�vel.");
								return false;
							}
							Set<Player> players = PlayerUtils.getPlayersOutOfMatch();
							for(Player target : players) {
								p.sendMessage("�aConectando �e" + player.getName() + " �aem uma partida.");
								target.sendMessage("�aVoc� foi conectado a uma partida.");
							}
							return true;
						} else {
							myself = false;
							player = Bukkit.getPlayer(args[2]);
							if(player == null) {
								return this.playerOffline(s, args[2]);
							}
						}
					}
				}
				if(!MatchsManager.instance.getMatch(id).canJoinPlayers()) {
					p.sendMessage("�cEssa partida j� est� em andamento ou est� indispon�vel.");
					return false;
				}
				if(MatchsManager.instance.playerIsInMatch(player)) {
					if(myself) {
						p.sendMessage("�cVoc� j� est� em uma partida qualquer.");
					} else {
						p.sendMessage("�cEsse jogador j� est� em uma partida qualquer.");
					}
					return false;
				}
				MatchsManager.instance.getMatch(id).joinPlayer(player);
				if(!myself) {
					p.sendMessage("�aConectando �e" + player.getName() + " �aem uma partida.");
					player.sendMessage("�aVoc� foi conectado a uma partida.");
				}
				return true;
			} else if(args[0].equals("quit")) {
				if(args.length == 1) {
					return this.missingArguments(s, "/cpt quit <jogador>");
				}
				Player player = Bukkit.getPlayer(args[1]);
				if(player == null) {
					return this.playerOffline(s, args[1]);
				}
				if(!MatchsManager.instance.playerIsInMatch(player)) {
					p.sendMessage("�cEsse jogador n�o est� em nenhuma partida.");
					return false;
				}
				MatchsManager.instance.disconnectPlayerOfMatch(player);
				p.sendMessage("�aJogador retirado da partida com �xito.");
				player.sendMessage("�cVoc� foi desconectado da partida.");
				return true;
			} else if(args[0].equals("forcestart")) {
				if(args.length == 1) {
					return this.missingArguments(s, "/cpt forcestart <id>");
				}
				int id;
				try {
					id = Integer.valueOf(args[1]);
				} catch(NumberFormatException e) {
					return this.invalidNumberValue(s, args[1]);
				}
				if(!MatchsManager.instance.hasMatch(id)) {
					p.sendMessage("�cN�o existe uma partida com esse ID.");
					return false;
				}
				Match match = MatchsManager.instance.getMatch(id);
				if(!match.waitingOrStarting()) {
					p.sendMessage("�cEssa partida j� est� em andamento.");
					return false;
				}
				match.setLeftTimeToStart(0);
				p.sendMessage("�aTempo no cronometro definido para 0 segundos.");
				return true;
			} else if(args[0].equals("releaseall")) {
				if(!MatchsManager.instance.playerIsInMatch(p)) {
					p.sendMessage("�cVoc� n�o est� em nenhuma partida.");
					return false;
				}
				Match match = MatchsManager.instance.getMatchOfPlayer(p);
				if(match.getStatus() != MatchStatus.RUNNING) {
					p.sendMessage("�cSua partida n�o est� em andamento ainda ou est� sendo finalizada.");
					return false;
				}
				Capturer player = match.getCasted(p);
				if(!player.isCarrying()) {
					p.sendMessage("�cVoc� n�o est� carregando ningu�m.");
					return false;
				}
				player.releaseAll();
				return true;
			} else {
				return this.invalidArgument(s, args[0]);
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(Player p, String[] args) {
		if(args.length == 1) {
			return ListUtils.getElementsStartingWith(args[0], "reload", "setlobby", "setspawn", "setredbase", "setbluebase", "mapinfo", "addparty", "finalize", "partys", "join", "quit", "forcestart", "releaseall");
		}
		if(args[0].equals("setredbase") || args[0].equals("setbluebase")) {
			if(args.length == 2) {
				if(Builder.hasBuilder(p) && Builder.getBuilder(p).hasLocationsSetted()) {
					return ListUtils.getElementsStartingWith(args[1], "pos1", "pos2", "build");
				} else {
					return ListUtils.getElementsStartingWith(args[1], "pos1", "pos2");
				}
			}
		} else if(args[0].equals("addparty")) {
			if(args.length == 2) {
				ArrayList<String> names = new ArrayList<>();
				MapTheme.availableMaps.forEach(map -> names.add(map.getMapName()));
				return ListUtils.getElementsStartingWith(args[1], names);
			}
		} else if(args[0].equals("finalize")) {
			if(args.length == 2) {
				ArrayList<String> ids = new ArrayList<>();
				MatchsManager.instance.getMatchs().forEach(match -> ids.add(String.valueOf(match.getId())));
				return ListUtils.getElementsStartingWith(args[1], ids);
			}
		} else if(args[0].equals("join")) {
			if(args.length == 2) {
				ArrayList<String> ids = new ArrayList<>();
				MatchsManager.instance.getMatchs().forEach(match -> ids.add(String.valueOf(match.getId())));
				return ListUtils.getElementsStartingWith(args[1], ids);
			}
			if(args.length == 3) {
				return ListUtils.getElementsStartingWith(args[2], true, PlayerUtils.getOnlinePlayersName());
			}
		} else if(args[0].equals("quit")) {
			if(args.length == 2) {
				return ListUtils.getElementsStartingWith(args[1], true, PlayerUtils.getOnlinePlayersName());
			}
		} else if(args[0].equals("forcestart")) {
			if(args.length == 2) {
				ArrayList<String> ids = new ArrayList<>();
				for(Match match : MatchsManager.instance.getMatchs()) {
					if(match.waitingOrStarting()) {
						ids.add(String.valueOf(match.getId()));
					}
				}
				return ListUtils.getElementsStartingWith(args[1], ids);
			}
		}
		return null;
	}
	
}