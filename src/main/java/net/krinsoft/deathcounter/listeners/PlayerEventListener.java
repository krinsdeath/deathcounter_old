package net.krinsoft.deathcounter.listeners;

import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.types.DeathPlayer;
import net.krinsoft.deathcounter.util.DeathLogger;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventListener extends PlayerListener {
	public DeathCounter plugin;
	public DeathLogger log;
	
	public PlayerEventListener(DeathCounter instance) {
		plugin = instance;
		log = plugin.log;
	}
	
	@Override
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (plugin.players.get(event.getPlayer()) == null) {
			plugin.players.put(event.getPlayer(), new DeathPlayer(plugin, event.getPlayer().getName()));
		}
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (plugin.players.get(event.getPlayer()) != null) {
			plugin.players.get(event.getPlayer()).save();
			plugin.players.remove(event.getPlayer());
		}
	}

}