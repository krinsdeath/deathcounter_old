package net.krinsoft.deathcounter.util;

import java.util.HashMap;
import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.types.DeathPlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class DeathTimer
        implements Runnable {

    private DeathCounter plugin;

    public DeathTimer(DeathCounter instance) {
        this.plugin = instance;
    }

    public void run() {
        saveAll();
    }

    public void saveAll() {
        String def = this.plugin.config.getString("settings.storage.type", "yaml");
        if (def.equalsIgnoreCase("yaml")) {
            this.plugin.users.save();
        } else if (def.equalsIgnoreCase("sqlite")) {
            for (Player player : this.plugin.getServer().getOnlinePlayers()) {
                if (this.plugin.players.get(player.getName()) != null) {
                    ((DeathPlayer) this.plugin.players.get(player.getName())).save();
                }
            }
        } else if (!def.equalsIgnoreCase("MySQL"));
    }
}
