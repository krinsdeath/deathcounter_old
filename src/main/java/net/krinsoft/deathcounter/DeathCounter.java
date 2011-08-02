package net.krinsoft.deathcounter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nijikokun.register.payment.Method;

import net.krinsoft.deathcounter.listeners.CommandListener;
import net.krinsoft.deathcounter.listeners.EntityEventListener;
import net.krinsoft.deathcounter.listeners.PlayerEventListener;
import net.krinsoft.deathcounter.listeners.ServerEventListener;
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
	public Settings settings;
	
	// listeners
	private final EntityEventListener eListener = new EntityEventListener(this);
	private final PlayerEventListener pListener = new PlayerEventListener(this);
	private final CommandListener cListener = new CommandListener(this);
	private final ServerEventListener sListener = new ServerEventListener(this);
	
	// instance variables
	public HashMap<Player, DeathPlayer> players = new HashMap<Player, DeathPlayer>();
	public Method method = null;
	protected PluginDescriptionFile description;
	protected PluginManager manager;
	protected Plugin plugin;
	
	public List<String> monsters = new ArrayList<String>();

	public boolean perm;
	public boolean eco;

	@Override
	public void onEnable() {
		// initialize the instance stuff
		plugin = this;
		description = this.getDescription();
		manager = this.getServer().getPluginManager();
		
		// initialize the configuration
		settings = new Settings(this);
		leaders = new Leaderboards(this);
		
		// iConomy support stuff
		manager.registerEvent(Event.Type.PLUGIN_ENABLE, sListener, Event.Priority.Monitor, this);
		manager.registerEvent(Event.Type.PLUGIN_DISABLE, sListener, Event.Priority.Monitor, this);
		
		// First up, we have... EntityListener!
		manager.registerEvent(Event.Type.ENTITY_DEATH, eListener, Event.Priority.Normal, this);
		
		// Next, and certainly appreciated... PlayerListener!
		manager.registerEvent(Event.Type.PLAYER_LOGIN, pListener, Event.Priority.Normal, this);
		manager.registerEvent(Event.Type.PLAYER_QUIT, pListener, Event.Priority.Normal, this);
		
		// And lastly, Command Listener!
		getCommand("deathcount").setExecutor(cListener);
		
		long n = config.getInt("settings.save_interval", 30) * 60000; // Multiply the value in the config by 60000 ms, or 60 seconds
		this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new DeathTimer(this), n, n);

		initMonsters();
		
		// report that we finished with the enable
		log.info(info("fullname") + " by " + info("authors") + " enabled!");
	}

	@Override
	public void onDisable() {
		if (config.getString("settings.storage.type", "yaml").equalsIgnoreCase("yaml")) {
			users.save();
		} else if (config.getString("settings.storage.type", "yaml").equalsIgnoreCase("sqlite")) {
			for (Player player : this.getServer().getOnlinePlayers()) {
				if (players.get(player) != null) {
					players.get(player).save();
				}
			}
		} else if (config.getString("settings.storage.type", "yaml").equalsIgnoreCase("mysql")) {
			
		}
		this.getServer().getScheduler().cancelTasks(this);
		log.info("disabled");
	}
	
	/**
	 * Unique methods to this class
	 */

	public String info(String field) {
		if (field.equalsIgnoreCase("name")) {
			return description.getName();
		}
		if (field.equalsIgnoreCase("fullname")) {
			return description.getFullName();
		}
		if (field.equalsIgnoreCase("version")) {
			return description.getVersion();
		}
		if (field.equalsIgnoreCase("authors")) {
			return description.getAuthors().toString();
		}
		return description.getName();
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
}
