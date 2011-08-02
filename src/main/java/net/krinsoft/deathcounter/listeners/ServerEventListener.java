package net.krinsoft.deathcounter.listeners;

import com.nijikokun.register.payment.Methods;

import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.util.DeathLogger;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

public final class ServerEventListener extends ServerListener {
	private final DeathCounter plugin;
	private final DeathLogger log;

	private Methods methods = null;

	public ServerEventListener(DeathCounter instance) {
		plugin = instance;
		log = plugin.log;
		methods = new Methods();
	}

	@Override
    public void onPluginDisable(PluginDisableEvent event) {
        if (methods != null && methods.hasMethod()) {
			boolean check = methods.checkDisabled(event.getPlugin());
            if (check) {
                log.info("Unhooking from " + plugin.method.getName() + " v" + plugin.method.getVersion() + "...");
                plugin.method = null;
            }
        }
    }

	@Override
    public void onPluginEnable(PluginEnableEvent event) {
        if (!methods.hasMethod()) {
			if (methods.setMethod(event.getPlugin())) {
				plugin.method = methods.getMethod();
				plugin.eco = true;
				log.info("Economy system found! Hooking " + plugin.method.getName() + " v" + plugin.method.getVersion() + "...");
			}
        }
    }

}
