package net.krinsoft.deathcounter.listeners;

import com.iConomy.iConomy;

import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.util.DeathLogger;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

public class ServerEventListener extends ServerListener {
	private DeathCounter plugin;
	private DeathLogger log;

	public ServerEventListener(DeathCounter instance) {
		plugin = instance;
		log = plugin.log;
	}
	
    public void onPluginDisable(PluginDisableEvent event) {
        if (plugin.iConomy != null) {
            if (event.getPlugin().getDescription().getName().equals("iConomy")) {
                plugin.iConomy = null;
                log.info("Unhooked from iConomy.");
            }
        }
    }

    public void onPluginEnable(PluginEnableEvent event) {
        if (plugin.iConomy == null) {
            Plugin iConomy = plugin.getServer().getPluginManager().getPlugin("iConomy");

            if (iConomy != null) {
                if (iConomy.isEnabled() && iConomy.getClass().getName().equals("com.iConomy.iConomy")) {
                    plugin.iConomy = (iConomy)iConomy;
                   	plugin.ico = plugin.config.getBoolean("settings.iConomy", false);
                   	if (plugin.ico) {
                   		log.info("Successfully hooked iConomy.");
                   	} else {
                   		log.info("iConomy is disabled.");
                   	}
                } 
            }
        }
    }

}
