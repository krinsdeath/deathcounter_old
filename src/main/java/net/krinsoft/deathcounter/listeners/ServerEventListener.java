package net.krinsoft.deathcounter.listeners;

import com.fernferret.allpay.AllPay;
import java.util.Arrays;
import net.krinsoft.deathcounter.DeathCounter;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

public class ServerEventListener extends ServerListener {

    private DeathCounter plugin;

    public ServerEventListener(DeathCounter plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        if (Arrays.asList(AllPay.validEconPlugins).contains(event.getPlugin().getDescription().getName())) {
            this.plugin.setBank(this.plugin.getBanker().loadEconPlugin());
        }
    }
}
