package br.felipe.capture.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EEvent extends Event {
	
	private static final HandlerList HANDLERS = new HandlerList();
	
	public EEvent callEvent() {
		Bukkit.getServer().getPluginManager().callEvent(this);
		return this;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
}