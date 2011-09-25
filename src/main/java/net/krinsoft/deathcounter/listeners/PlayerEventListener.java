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
        this.plugin = instance;
        this.log = this.plugin.log;
    }

    @Override
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (this.plugin.players.get(event.getPlayer().getName()) == null) {
            this.plugin.players.put(event.getPlayer().getName(), new DeathPlayer(this.plugin, event.getPlayer().getName()));
        }
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (this.plugin.players.get(event.getPlayer().getName()) != null) {
            ((DeathPlayer) this.plugin.players.get(event.getPlayer().getName())).save();
            this.plugin.players.remove(event.getPlayer().getName());
        }
    }
}
