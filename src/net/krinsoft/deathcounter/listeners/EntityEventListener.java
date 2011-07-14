package net.krinsoft.deathcounter.listeners;

import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.types.DeathPlayer;
import net.krinsoft.deathcounter.util.DeathLogger;

import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

public class EntityEventListener extends EntityListener {
	public DeathCounter plugin;
	public DeathLogger log;
	
	public EntityEventListener(DeathCounter instance) {
		plugin = instance;
		log = plugin.log;
	}
	
	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		Event eventType = event.getEntity().getLastDamageCause();
		if (eventType instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent evt = (EntityDamageByEntityEvent) eventType;
			if (evt.getDamager() instanceof Player) {
				Player source = (Player) evt.getDamager();
				if (plugin.players.get(source) == null) {
					plugin.players.put(source, new DeathPlayer(plugin, source.getName()));
				}
				/*
				 * Entity Damage By Entity Event
				 * 
				 * -> source Player
				 */
				String targetName = "";
				if (event.getEntity() instanceof Wolf) { targetName = "wolf"; }
				if (event.getEntity() instanceof Player) { targetName = "player"; }
				else { targetName = event.getEntity().toString().toLowerCase().replaceAll("craft", ""); }
				plugin.players.get(source).add(source, targetName);
				// debugger
				// log.info(targetName + "->" + event.getEntity().toString());
			} else {
				return;
			}
		} else if (eventType instanceof EntityDamageByProjectileEvent) {
			EntityDamageByProjectileEvent evt = (EntityDamageByProjectileEvent) eventType;
			if (evt.getDamager() instanceof Player) {
				Player source = (Player) evt.getDamager();
				/*
				 * Entity Damage By Projectile Event
				 * 
				 * -> source Player
				 */
				String targetName = "";
				if (event.getEntity() instanceof Wolf) { targetName = "wolf"; }
				if (event.getEntity() instanceof Player) { targetName = "player"; }
				else { targetName = event.getEntity().toString().toLowerCase().replaceAll("craft", ""); }
				plugin.players.get(source).add(source, targetName);
				// debugger
				// log.info(targetName + "->" + event.getEntity().toString());
			} else {
				return;
			}
		}
	}

}
