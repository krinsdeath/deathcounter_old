package net.krinsoft.deathcounter.util;

import net.krinsoft.deathcounter.DeathCounter;

import org.bukkit.entity.Player;

public class DeathTimer implements Runnable {
	private DeathCounter plugin;
	
	public DeathTimer(DeathCounter instance) {
		plugin = instance;
	}
	
	public void run() {
		saveAll();
	}
	
	public void saveAll() {
		if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("yaml")) {
			plugin.users.save();
		} else if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("sqlite")) {
			for (Player player : plugin.getServer().getOnlinePlayers()) {
				if (plugin.players.get(player) != null) {
					plugin.players.get(player).save();
				}
			}
		} else if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("MySQL")) {
			
		}
	}

}
