package br.felipe.capture.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import br.felipe.capture.Main;
import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;

public class ActionBar {
	
	public static void sendActionBar(String actionBar) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			sendActionBar(player, actionBar);
		}
	}
	
	public static void sendActionBar(ArrayList<Player> to, String actionBar) {
		for(Player player : to) {
			sendActionBar(player, actionBar);
		}
	}
	
	public static void sendActionBar(Player to, String actionBar) {
		if(ActionBarProfile.hasProfile(to.getUniqueId())) {
			ActionBarProfile profile = ActionBarProfile.getProfile(to.getUniqueId());
			profile.enabled = false;
			new BukkitRunnable() {
				@Override
				public void run() {
					profile.enabled = true;
				}
			}.runTaskLater(Main.getPlugin(), 65l);
			
		}
		secretActionBar(to, actionBar);
	}
	
	private static void secretActionBar(Player to, String actionBar) {
		((CraftPlayer) to).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer.a(Formats.toJSON(actionBar)), ChatMessageType.GAME_INFO, to.getUniqueId()));
	}
	
	public static void sendActionBar(Player to, String actionBar, int seconds) {
		if(ActionBarProfile.hasProfile(to.getUniqueId())) {
			ActionBarProfile profile = ActionBarProfile.getProfile(to.getUniqueId());
			profile.enabled = false;
			new BukkitRunnable() {
				@Override
				public void run() {
					profile.enabled = true;
				}
			}.runTaskLater(Main.getPlugin(), (20l * seconds) + 65l);
			
		}
		new BukkitRunnable() {
			long time = System.currentTimeMillis() + ((long) seconds * 1000l);
			@Override
			public void run() {
				if(System.currentTimeMillis() >= time) {
					this.cancel();
					return;
				}
				if(to != null) {
					secretActionBar(to, actionBar);
				}
			}
		}.runTaskTimer(Main.getPlugin(), 0l, 1l);
	}
	
	public static class ActionBarProfile {
		
		public static final HashSet<ActionBarProfile> profiles = new HashSet<>();
		
		private UUID owner;
		private ArrayList<ObjectMap> texts;
		
		private String splitter;
		
		private boolean enabled;
		
		private BukkitTask task;
		private long delay = 1l;
		
		public ActionBarProfile(Player owner) {
			this(owner.getUniqueId());
		}
		
		public ActionBarProfile(UUID owner) {
			this.owner = owner;
			this.texts = new ArrayList<>();
			this.enabled = true;
			this.task = new BukkitRunnable() {
				@Override
				public void run() {
					if(enabled && getPlayerOwner() != null && !getNaturalText().isEmpty()) {
						secretActionBar(getPlayerOwner(), getText());
					}
				}
			}.runTaskTimer(Main.getPlugin(), delay, delay);
		}
		
		public UUID getUUIDOwner() {
			return owner;
		}
		
		public Player getPlayerOwner() {
			return Bukkit.getPlayer(owner);
		}
		
		public String getSplitter() {
			return splitter;
		}
		
		public void setSplitter(@Nullable String splitter) {
			this.splitter = splitter;
		}
		
		public ActionBarProfile setEnabled(boolean enabled) {
			this.enabled = enabled;
			return this;
		}
		
		public ArrayList<ObjectMap> getTextsMap() {
			return texts;
		}
		
		public ObjectMap getObjectMap(String key) {
			for(ObjectMap o : texts) {
				if(o.getKey().equals(key)) {
					return o;
				}
			}
			return null;
		}
		
		public String getText() {
			StringBuilder sb = new StringBuilder();
			int index = 0;
			for(ObjectMap o : texts) {
				sb.append((String) o.getValue() + (index == texts.size() - 1 ? "" : (splitter == null ? "" : splitter)));
				++index;
			}
			return sb.toString();
		}
		
		public String getNaturalText() {
			StringBuilder sb = new StringBuilder();
			this.texts.forEach(o -> sb.append((String) o.getValue()));
			return sb.toString();
		}
		
		public boolean hasKey(String key) {
			return this.getObjectMap(key) != null;
		}
		
		public boolean hasText(String text, boolean ignoreCase) {
			for(ObjectMap o : texts) {
				if(ignoreCase ? ((String) o.getValue()).equalsIgnoreCase(text) : ((String) o.getValue()).equals(text)) {
					return true;
				}
			}
			return false;
		}
		
		public String getText(String key) {
			return (String) this.getObjectMap(key).getValue();
		}
		
		public ArrayList<String> getFragmentedTexts() {
			ArrayList<String> texts = new ArrayList<>();
			this.texts.forEach(o -> texts.add((String) o.getValue()));
			return texts;
		}
		
		public void appendText(String key, Object text) {
			if(hasKey(key)) {
				ObjectMap o = getObjectMap(key);
				int index = texts.indexOf(o);
				o.setValue(text);
				this.texts.set(index, o);
			} else {
				this.texts.add(new ObjectMap(key, String.valueOf(text)));
			}
		}
		
		public void removeText(String key) {
			this.texts.remove(this.getObjectMap(key));
		}
		
		public void clearText() {
			this.texts.clear();
		}
		
		public void setTexts(ArrayList<ObjectMap> texts) {
			this.texts = texts == null ? new ArrayList<>() : texts;
		}
		
		private ActionBarProfile stop() {
			this.task.cancel();
			return this;
		}
		
		public static boolean hasProfile(UUID uuid) {
			return getProfile(uuid) != null;
		}
		
		public static ActionBarProfile getProfile(UUID uuid) {
			for(ActionBarProfile profile : profiles) {
				if(profile.owner.equals(uuid)) {
					return profile;
				}
			}
			return null;
		}
		
		public static void removeProfile(UUID uuid) {
			profiles.remove(getProfile(uuid).stop());
		}
		
	}
	
}