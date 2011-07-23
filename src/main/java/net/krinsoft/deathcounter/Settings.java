package net.krinsoft.deathcounter;

import com.nijikokun.bukkit.Permissions.Permissions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import net.krinsoft.deathcounter.util.DeathLogger;

import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;

public class Settings {
	private DeathCounter plugin;
	private DeathLogger log;
	
	private File dataFolder;
	
	public Settings(DeathCounter instance) {
		plugin = instance;
		log = plugin.log;
		setup();
	}

	public void setup() {
		// get the data folder and make sure it exists
		dataFolder = plugin.getDataFolder();
		dataFolder.mkdirs();
		// get the config file
		File config = makeDefaults(new File(dataFolder, "config.yml"));
		
		plugin.config = new Configuration(config);
		plugin.config.load();
		
		if (plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("yaml")) {
			plugin.users = new Configuration(new File(dataFolder, plugin.config.getString("settings.storage.info.filename", "users.yml")));
			plugin.users.load();
		} else if (plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("sqlite")) {
			makeDefaultSqliteDatabase();
			if (plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("yaml")) {
				plugin.users = new Configuration(new File(dataFolder, plugin.config.getString("settings.storage.info.filename", "users.yml")));
				plugin.users.load();
			}
		} else if (plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("mysql")) {
			// TODO Implement MySQL
		}
		if (plugin.config.getBoolean("settings.permissions", false)) {
			plugin.perm = setupPermissions();
		}
	}

	private boolean setupPermissions() {
		Plugin permPlugin = plugin.getServer().getPluginManager().getPlugin("Permissions");
		if (plugin.permissions == null) {
			if (permPlugin != null) {
				plugin.permissions = ((Permissions) permPlugin).getHandler();
				log.info("Permissions found.");
				return true;
			} else {
				log.warn("Permissions not found.");
				return false;
			}
		}
		return false;
	}

	private void makeDefaultSqliteDatabase() {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/DeathCounter/users.db");
			String initQuery =  "CREATE TABLE IF NOT EXISTS `users` " +
								"(id INTEGER," +
								" name VARCHAR(32)," +
								" cow INTEGER DEFAULT 0," +
								" pig INTEGER DEFAULT 0," +
								" sheep INTEGER DEFAULT 0," +
								" chicken INTEGER DEFAULT 0," +
								" squid INTEGER DEFAULT 0," +
								" zombie INTEGER DEFAULT 0," +
								" skeleton INTEGER DEFAULT 0," +
								" ghast INTEGER DEFAULT 0," +
								" creeper INTEGER DEFAULT 0," +
								" slime INTEGER DEFAULT 0," +
								" spider INTEGER DEFAULT 0," +
								" wolf INTEGER DEFAULT 0," +
								" pigzombie INTEGER DEFAULT 0," +
								" player INTEGER DEFAULT 0," +
								" PRIMARY KEY(id DESC));";
			Statement state = conn.createStatement();
			@SuppressWarnings("unused")
			int rs = state.executeUpdate(initQuery);
		} catch (ClassNotFoundException e) {
			log.warn("SQLite JDBC Driver not found. Defaulting to YAML.");
			plugin.config.setProperty("settings.storage.type", "yaml");
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private File makeDefaults(File file) {
		if (!file.exists()) {
			new File(file.getParent()).mkdirs();
			InputStream in = DeathCounter.class.getResourceAsStream("/defaults/" + file.getName());
			if (in != null) {
				FileOutputStream out = null;
				try {
					out = new FileOutputStream(file);
					byte[] buffer = new byte[2048];
					int length = 0;
					while ((length = in.read(buffer)) > 0) {
						out.write(buffer, 0, length);
					}
				} catch (IOException e) {
					log.warn("Error creating " + file.getName());
					e.printStackTrace();
				} finally {
					try {
						in.close();
						out.close();
					} catch (IOException e) {
						log.warn("Error closing stream.");
						e.printStackTrace();
					}
				}
			}
		}
		return file;
	}
}
