package net.krinsoft.deathcounter.listeners;

import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.types.DeathPlayer;
import net.krinsoft.deathcounter.util.DeathLogger;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

public class EntityEventListener extends EntityListener {

    public DeathCounter plugin;
    public DeathLogger log;

    public EntityEventListener(DeathCounter instance) {
        this.plugin = instance;
        this.log = this.plugin.log;
    }

    @Override
    public void onEntityDeath(EntityDeathEvent event) {
        Event eventType = event.getEntity().getLastDamageCause();
        if ((eventType instanceof EntityDamageByEntityEvent)) {
            EntityDamageByEntityEvent evt = (EntityDamageByEntityEvent) eventType;
            Entity checked = checkSource(evt.getDamager());
            if ((checked != null) && ((checked instanceof Player))) {
                Player source = (Player) checked;
                if (this.plugin.players.get(source.getName()) == null) {
                    this.plugin.players.put(source.getName(), new DeathPlayer(this.plugin, source.getName()));
                }

                String targetName = "";
                if (event.getEntity() instanceof Wolf) { targetName = "wolf"; }
                if (event.getEntity() instanceof Player) { targetName = "player"; }
                if (event.getEntity() instanceof Enderman) { targetName = "enderman"; }
                if (event.getEntity() instanceof Silverfish) { targetName = "silverfish"; }
                if (event.getEntity() instanceof CaveSpider) { targetName = "cavespider"; }
                else { targetName = event.getEntity().toString().toLowerCase().replaceAll("craft", ""); }
                plugin.players.get(source.getName()).add(source, targetName);
            } else {
                return;
            }
        }
    }

    public Entity checkSource(Entity source) {
        if ((source instanceof Player)) {
            return source;
        }
        if (((source instanceof Projectile)) && ((((Projectile) source).getShooter() instanceof Player))) {
            return ((Projectile) source).getShooter();
        }
        return null;
    }
}
