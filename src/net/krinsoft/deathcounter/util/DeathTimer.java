package net.krinsoft.deathcounter.util;

import java.util.TimerTask;

import net.krinsoft.deathcounter.DeathCounter;

import org.bukkit.entity.Player;

public class DeathTimer extends TimerTask {
	public DeathCounter plugin;
	
	public DeathTimer(DeathCounter instance) {
		plugin = instance;
	}
	
	@Override
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
