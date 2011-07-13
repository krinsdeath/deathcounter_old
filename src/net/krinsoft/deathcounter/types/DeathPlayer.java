package net.krinsoft.deathcounter.types;
/**
 * @author krinsdeath
 * 
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.interfaces.IDatabase;
import net.krinsoft.deathcounter.util.DeathLogger;

import org.bukkit.entity.Player;

public class DeathPlayer implements IDatabase {
	public DeathCounter plugin;
	public DeathLogger log;
	// This player's handle (his name)
	public String name;
	
	// Stats relevant to this player
	// animals
	public int cow;
	public int pig;
	public int squid;
	public int chicken;
	public int sheep; // currently bugged, craftbukkit doesn't fire ENTITY_DEATH when sheep die
	
	// monsters
	public int skeleton;
	public int zombie;
	public int creeper;
	public int spider;
	public int wolf;
	public int ghast;
	public int slime;
	public int pigzombie;
	
	// players
	public int player;
	public int total;
	
	/**
	 * DeathPlayer class constructor
	 * 
	 * @param instance
	 * the plugin this player is instantiated in
	 * @param name
	 * the name of the player associated with this data
	 */
	public DeathPlayer(DeathCounter instance, String name) {
		plugin = instance;
		log = plugin.log;
		this.name = name;
		load();
	}
	
	/**
	 * increments the kill count of the specified mob for the specified player by 1
	 * then inserts the updated entry into the plugin's player array
	 * 
	 * @param ply
	 * the player associated with this data
	 * @param mob
	 * the mob name to increment by one
	 */
	public void add(Player ply, String mob) {
		if (mob.equals("pig")) {
			pig++;
			update("pig");
		} else if (mob.equals("cow")) {
			cow++;
			update("cow");
		} else if (mob.equals("sheep")) {
			sheep++;
			update("sheep");
		} else if (mob.equals("squid")) {
			squid++;
			update("squid");
		} else if (mob.equals("chicken")) {
			chicken++;
			update("chicken");
		} else if (mob.equals("skeleton")) {
			skeleton++;
			update("skeleton");
		} else if (mob.equals("zombie")) {
			zombie++;
			update("zombie");
		} else if (mob.equals("wolf")) {
			wolf++;
			update("wolf");
		} else if (mob.equals("spider")) {
			spider++;
			update("spider");
		} else if (mob.equals("ghast")) {
			ghast++;
			update("ghast");
		} else if (mob.equals("creeper")) {
			creeper++;
			update("creeper");
		} else if (mob.equals("slime")) {
			slime++;
			update("slime");
		} else if (mob.equals("pigzombie")) {
			pigzombie++;
			update("pigzombie");
		} else if (mob.equals("player")) {
			player++;
			update("player");
		}
		total++;
		update("total");
		plugin.players.put(ply, this);
	}
	
	public void load() {
		if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("yaml")) {
			loadYaml();
		} else if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("sqlite")) {
			loadSqlite();
		} else if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("mysql")) {
			loadMysql();
		}
	}
	
	private void loadYaml() {
		// animals
		pig = plugin.users.getInt(name + ".pig", 0);
		cow = plugin.users.getInt(name + ".cow", 0);
		sheep = plugin.users.getInt(name + ".sheep", 0);
		chicken = plugin.users.getInt(name + ".chicken", 0);
		squid = plugin.users.getInt(name + ".squid", 0);
		// monsters
		skeleton = plugin.users.getInt(name + ".skeleton", 0);
		zombie = plugin.users.getInt(name + ".zombie", 0);
		ghast = plugin.users.getInt(name + ".ghast", 0);
		wolf = plugin.users.getInt(name + ".wolf", 0);
		creeper = plugin.users.getInt(name + ".creeper", 0);
		slime = plugin.users.getInt(name + ".slime", 0);
		pigzombie = plugin.users.getInt(name + ".pigzombie", 0);
		// players
		player = plugin.users.getInt(name + ".player", 0);
		total = plugin.users.getInt(name + ".total", 0);
		log.info("player " + name + " loaded");
	}

	private void loadSqlite() {
		try {
			String query = "SELECT * FROM `users` WHERE `name` = '" + name + "';";
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/DeathCounter/users.db");
			Statement state = conn.createStatement();
			ResultSet rs = state.executeQuery(query);
			rs.next();
			if (rs.getRow() == 0) {
				insert();
				rs = state.executeQuery(query);
				rs.next();
				if (rs.getRow() == 0) {
					log.warn("Something is wrong.");
					log.warn("User creation aborted.");
					return;
				}
			}
			pig = rs.getInt("pig");
			cow = rs.getInt("cow");
			sheep = rs.getInt("sheep");
			chicken = rs.getInt("chicken");
			squid = rs.getInt("squid");
			skeleton = rs.getInt("skeleton");
			zombie = rs.getInt("zombie");
			ghast = rs.getInt("ghast");
			wolf = rs.getInt("wolf");
			creeper = rs.getInt("creeper");
			slime = rs.getInt("slime");
			pigzombie = rs.getInt("pigzombie");
			player = rs.getInt("player");
			state.close();
			conn.close();
			log.info("player " + name + " loaded");
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void loadMysql() {
		
	}

	public void save() {
		if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("yaml")) {
			saveYaml();
		} else if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("sqlite")) {
			saveSqlite();
		} else if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("mysql")) {
			saveMysql();
		}
	}
	
	private void saveYaml() {
		plugin.users.setProperty(name + ".pig", pig);
		plugin.users.setProperty(name + ".cow", cow);
		plugin.users.setProperty(name + ".sheep", sheep);
		plugin.users.setProperty(name + ".squid", squid);
		plugin.users.setProperty(name + ".chicken", chicken);
		plugin.users.setProperty(name + ".skeleton", skeleton);
		plugin.users.setProperty(name + ".zombie", zombie);
		plugin.users.setProperty(name + ".ghast", ghast);
		plugin.users.setProperty(name + ".wolf", wolf);
		plugin.users.setProperty(name + ".creeper", creeper);
		plugin.users.setProperty(name + ".slime", slime);
		plugin.users.setProperty(name + ".pigzombie", pigzombie);
		plugin.users.setProperty(name + ".player", player);
		plugin.users.setProperty(name + ".total", total);
		plugin.users.save();
		if (plugin.config.getInt("settings.log_verbosity", 1) >= 3) {
			log.info("player " + name + " saved successfully");
		}
	}

	private void saveSqlite() {
		try {
			String query = "UPDATE `users` SET" +
					"`pig` = " + pig + "," +
					"`cow` = " + cow + "," +
					"`sheep` = " + sheep + "," +
					"`chicken` = " + chicken + "," +
					"`squid` = " + squid + "," +
					"`skeleton` = " + skeleton + "," +
					"`zombie` = " + zombie + "," +
					"`ghast` = " + ghast + "," +
					"`wolf` = " + wolf + "," +
					"`creeper` = " + creeper + "," +
					"`slime` = " + slime + "," +
					"`pigzombie` = " + pigzombie + "," +
					"`player` = " + player + " " +
					"WHERE `name` = '" + name + "';";
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/DeathCounter/users.db");
			Statement state = conn.createStatement();
			int rs = state.executeUpdate(query);
			if (rs == 0) {
				if (plugin.config.getInt("settings.log_verbosity", 1) >= 3) {
					log.info("player " + name + " saved successfully");
				}
			}
			state.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void saveMysql() {
		// TODO Auto-generated method stub
		// TODO Implement MySQL
		
	}

	/**
	 * an interface to the database for inputting a new user
	 * 
	 * uses a default entry
	 */
	public void insert() {
		if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("yaml")) {
			insertYaml();
		} else if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("sqlite")) {
			insertSqlite();
		} else if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("mysql")) {
			insertMysql();
		}
	}
	
	/**
	 * Creates a new user entry for this user in the YAML database
	 * using default values
	 */	
	private void insertYaml() {
		plugin.users.setProperty(name + ".pig", 0);
		plugin.users.setProperty(name + ".cow", 0);
		plugin.users.setProperty(name + ".sheep", 0);
		plugin.users.setProperty(name + ".squid", 0);
		plugin.users.setProperty(name + ".chicken", 0);
		plugin.users.setProperty(name + ".skeleton", 0);
		plugin.users.setProperty(name + ".zombie", 0);
		plugin.users.setProperty(name + ".ghast", 0);
		plugin.users.setProperty(name + ".wolf", 0);
		plugin.users.setProperty(name + ".creeper", 0);
		plugin.users.setProperty(name + ".slime", 0);
		plugin.users.setProperty(name + ".pigzombie", 0);
		plugin.users.setProperty(name + ".player", 0);
		plugin.users.setProperty(name + ".total", 0);
		plugin.users.save();
		if (plugin.config.getInt("settings.log_verbosity", 1) >= 2) {
			log.info("player " + name + " created");
		}
	}

	/**
	 * Creates a new user entry for this user in the SQLite database
	 * using default values
	 */
	private void insertSqlite() {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/DeathCounter/users.db");
			Statement state = conn.createStatement();
			int rs = state.executeUpdate("INSERT INTO `users` (`name`) VALUES('" + name + "');");
			if (rs == 0) {
				if (plugin.config.getInt("settings.log_verbosity", 1) >= 2) {
					log.info("player " + name + " created");
				}
			}
			state.close();
			conn.close();
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new user entry for this user in the MySQL database
	 * using default values
	 */
	private void insertMysql() {
		// TODO Auto-generated method stub
		// TODO Implement MySQL
		
	}

	/**
	 * an interface to the various save methods
	 * 
	 * @param mob
	 * the mob name to update
	 */
	public void update(String mob) {
		if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("yaml")) {
			updateYaml(mob);
		} else if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("sqlite")) {
			updateSqlite(mob);
		} else if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("mysql")) {
			updateMysql(mob);
		}
	}
	
	/**
	 * update this user's entry in the YAML database
	 * 
	 * @param mob
	 * the mob name to update
	 */
	public void updateYaml(String mob) {
		int update = 0;
		if (mob.equals("pig")) {
			update = pig;
		} else if (mob.equals("cow")) {
			update = cow;
		} else if (mob.equals("sheep")) {
			update = sheep;
		} else if (mob.equals("squid")) {
			update = squid;
		} else if (mob.equals("chicken")) {
			update = chicken;
		} else if (mob.equals("skeleton")) {
			update = skeleton;
		} else if (mob.equals("zombie")) {
			update = zombie;
		} else if (mob.equals("wolf")) {
			update = wolf;
		} else if (mob.equals("spider")) {
			update = spider;
		} else if (mob.equals("ghast")) {
			update = ghast;
		} else if (mob.equals("creeper")) {
			update = creeper;
		} else if (mob.equals("slime")) {
			update = slime;
		} else if (mob.equals("pigzombie")) {
			update = pigzombie;
		} else if (mob.equals("player")) {
			update = player;
		} else if (mob.equals("total")) {
			update = total;
		}
		plugin.users.setProperty(name + "." + mob, update);
		if (plugin.config.getInt("settings.log_verbosity", 1) >= 3) {
			log.info(name + "'s kill count for " + mob + " has been updated to " + update);
		}
	}
	
	/**
	 * update this user's entry in the SQLite database
	 * 
	 * @param mob
	 * the mob name to update
	 */
	public void updateSqlite(String mob) {
		int update = 0;
		if (mob.equals("pig")) {
			update = pig;
		} else if (mob.equals("cow")) {
			update = cow;
		} else if (mob.equals("sheep")) {
			update = sheep;
		} else if (mob.equals("squid")) {
			update = squid;
		} else if (mob.equals("chicken")) {
			update = chicken;
		} else if (mob.equals("skeleton")) {
			update = skeleton;
		} else if (mob.equals("zombie")) {
			update = zombie;
		} else if (mob.equals("wolf")) {
			update = wolf;
		} else if (mob.equals("spider")) {
			update = spider;
		} else if (mob.equals("ghast")) {
			update = ghast;
		} else if (mob.equals("creeper")) {
			update = creeper;
		} else if (mob.equals("slime")) {
			update = slime;
		} else if (mob.equals("pigzombie")) {
			update = pigzombie;
		} else if (mob.equals("player")) {
			update = player;
		} else if (mob.equals("total")) {
			return;
		}
		try {
			String query = "UPDATE `users` SET `" + mob + "` = " + update + " WHERE `name` = '" + name + "';";
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/DeathCounter/users.db");
			Statement state = conn.createStatement();
			int rs = state.executeUpdate(query);
			state.close();
			conn.close();
			if (plugin.config.getInt("settings.log_verbosity", 1) >= 3 && rs == 0) {
				log.info(name + "'s kill count for " + mob + " has been updated to " + update);
			}
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * update this user's entry in the MySQL database
	 * 
	 * @param mob
	 * the mob name to update
	 */
	public void updateMysql(String mob) {
		// TODO Auto-generated method stub
		// TODO Implement MySQL
		
	}
}
