package br.felipe.capture.events;

public class CEvent extends EEvent {
	
	protected boolean cancelled;
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
}