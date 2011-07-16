package net.krinsoft.deathcounter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import com.nijiko.permissions.PermissionHandler;

import net.krinsoft.deathcounter.listeners.CommandListener;
import net.krinsoft.deathcounter.listeners.EntityEventListener;
import net.krinsoft.deathcounter.listeners.PlayerEventListener;
import net.krinsoft.deathcounter.types.DeathPlayer;
import net.krinsoft.deathcounter.types.Leaderboards;
import net.krinsoft.deathcounter.util.DeathLogger;
import net.krinsoft.deathcounter.util.DeathTimer;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class DeathCounter extends JavaPlugin {
	// logger
	public final DeathLogger log = new DeathLogger(this);

	// configs
	public Configuration config;
	public Configuration users;
	public Leaderboards leaders;
	
	// listeners
	public final EntityEventListener eListener = new EntityEventListener(this);
	public final PlayerEventListener pListener = new PlayerEventListener(this);
	public final CommandListener cListener = new CommandListener(this);
	
	// instance variables
	public HashMap<Player, DeathPlayer> players = new HashMap<Player, DeathPlayer>();
	public PermissionHandler permissions;
	public PluginDescriptionFile description;
	public PluginManager manager;
	public Plugin plugin;
	
	// static stuff
	public static Timer timer = new Timer(true);
	
	public List<String> monsters = new ArrayList<String>();

	public boolean perm;

	public void onEnable() {
		// initialize the instance stuff
		plugin = this;
		description = this.getDescription();
		manager = this.getServer().getPluginManager();
		
		// initialize the configuration
		new Settings(this);
		leaders = new Leaderboards(this);
		
		// First up, we have... EntityListener!
		manager.registerEvent(Event.Type.ENTITY_DEATH, eListener, Event.Priority.Normal, this);
		
		// Next, and certainly appreciated... PlayerListener!
		manager.registerEvent(Event.Type.PLAYER_LOGIN, pListener, Event.Priority.Normal, this);
		manager.registerEvent(Event.Type.PLAYER_QUIT, pListener, Event.Priority.Normal, this);
		
		// And lastly, Command Listener!
		getCommand("deathcount").setExecutor(cListener);
		
		long n = config.getInt("settings.timer_interval", 30) * 60000; // Multiply the value in the config by 60000 ms, or 60 seconds
		timer.schedule(new DeathTimer(this), n, n);
		
		initMonsters();
		
		// report that we finished with the enable
		log.info("enabled");
	}
	
	private void initMonsters() {
		monsters.add("pig");
		monsters.add("cow");
		monsters.add("sheep");
		monsters.add("squid");
		monsters.add("chicken");
		monsters.add("skeleton");
		monsters.add("zombie");
		monsters.add("ghast");
		monsters.add("wolf");
		monsters.add("creeper");
		monsters.add("slime");
		monsters.add("pigzombie");
		monsters.add("spider");
		monsters.add("player");
	}

	public void onDisable() {
		if (config.getString("settings.storage.type").equalsIgnoreCase("yaml")) {
			users.save();
		} else if (config.getString("settings.storage.type").equalsIgnoreCase("sqlite")) {
			for (Player player : this.getServer().getOnlinePlayers()) {
				players.get(player).save();
			}
		} else if (config.getString("settings.storage.type").equalsIgnoreCase("mysql")) {
			
		}
		timer.cancel();
		log.info("disabled");
	}
	
	/**
	 * Unique methods to this class
	 */	

}
