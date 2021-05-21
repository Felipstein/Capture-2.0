package br.felipe.capture.command.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.felipe.capture.command.Command;
import br.felipe.capture.config.ConfigManager;
import br.felipe.capture.locations.MapTheme;

public class MapsCommand extends Command {
	
	public MapsCommand() {
		super("maps", true);
	}
	
	@Override
	public boolean onCommand(boolean console, CommandSender s, Player p, String[] args) {
		if(ConfigManager.instance.getMaps().isEmpty()) {
			p.sendMessage("§cNão existe nenhum mapa definido atualmente.");
		} else {
			p.sendMessage("§7Mapas carregados:");
			for(MapTheme map : MapTheme.mapsLoaded) {
				String color = "§c", suffix = "";
				if(MapTheme.isAvailable(map.getMapName())) {
					color = "§6";
				} else {
					suffix = " §8§o(em: partida " + MapTheme.getMatchIdOfMap(map.getMapName()) + ")";
				}
				p.sendMessage(color + " - §f" + map.getMapName() + suffix);
			}
			p.sendMessage("§7Mapas disponíveis:");
			for(MapTheme map : MapTheme.availableMaps) {
				p.sendMessage("§a - §f" + map.getMapName());
			}
		}
		return true;
	}
	
	@Override
	public List<String> onTabComplete(Player p, String[] args) {
		return null;
	}
	
}