package net.krinsoft.deathcounter.types;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.util.DeathLogger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Leaderboards {
	public DeathCounter plugin;
	public DeathLogger log;
	
	public Leaderboards(DeathCounter instance) {
		plugin = instance;
		log = plugin.log;
	}

	public void fetchYaml(CommandSender sender, String field, int loops) {
		playMessage(sender, "header", field, loops);
		if (field.equalsIgnoreCase("leaders")) {
			List<String> ldr = new ArrayList<String>();
			int i = 0;
			for (String key : plugin.users.getKeys()) {
				i = 0;
				while (true) {
					if (i >= ldr.size()) {
						ldr.add(key);
						break;
					} else if (plugin.users.getInt(key + ".total", 0) >= plugin.users.getInt(ldr.get(i) + ".total", 0)) {
						ldr.add(i, key);
						break;
					}
					i++;
				}
			}
			for (int x = 0; x < ldr.size(); x++) {
				if (x > loops) { break; }
				playMessage(sender, (x+1), ldr.get(x), field, plugin.users.getInt(ldr.get(x) + ".total", 0));
			}
		} else if (plugin.monsters.contains(field)) {
			List<String> ldr = new ArrayList<String>();
			int i = 0;
			for (String key : plugin.users.getKeys()) {
				i = 0;
				while (true) {
					if (i >= ldr.size()) {
						ldr.add(key);
						break;
					} else if (plugin.users.getInt(key + "." + field, 0) >= plugin.users.getInt(ldr.get(i) + "." + field, 0)) {
						ldr.add(i, key);
						break;
					}
					i++;
				}
			}
			for (int x = 0; x < ldr.size(); x++) {
				if (x > loops) { break; }
				playMessage(sender, (x+1), ldr.get(x), field, plugin.users.getInt(ldr.get(x) + "." + field, 0));
			}
		}
	}
	
	public void fetchSqlite(CommandSender sender, String field, int loops) {
		playMessage(sender, "header", field, loops);
		String query = "";
		int rank = 0; String name = ""; String key = "total"; int kills = 0;
		if (field.equalsIgnoreCase("leaders")) {
			query = "SELECT `name`, (pig + cow + sheep + chicken + squid + " +
					"skeleton + zombie + ghast + wolf + creeper + slime + " +
					"pigzombie + player) AS 'Total Kills' FROM `users` " +
					"ORDER BY 'Total Kills' DESC LIMIT " + loops + ";";
			field = "Total Kills";
			key = "total";
		} else if (plugin.monsters.contains(field)) {
			query = "SELECT `name`, `" + field + "` FROM `users` ORDER BY `" + field + "` DESC LIMIT " + loops + ";";
			key = field;
		}
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/DeathCounter/users.db");
			Statement state = conn.createStatement();
			ResultSet rs = state.executeQuery(query);
			while (rs.next()) {
				rank = rs.getRow();
				kills = rs.getInt(field);
				name = rs.getString("name");
				playMessage(sender, rank, name, key, kills);
				if (rs.getRow() > loops) { break; }
			}
			state.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void fetchMysql(CommandSender sender, String field, int loops) {
		sender.sendMessage("");
	}

	// Normal message
	private void playMessage(CommandSender sender, int rank, String name, String key, int kills) {
		String nm = "";
		if (sender instanceof Player) { nm = ((Player)sender).getName(); } 
		String msg = "#<rank> - <name> (<kills>)";
		if (rank == 1) { msg = plugin.config.getString("messages.first_rank", "#<rank> - <name> (<kills>)"); }
		if (rank == 2) { msg = plugin.config.getString("messages.second_rank", "#<rank> - <name> (<kills>)"); }
		if (rank == 3) { msg = plugin.config.getString("messages.third_rank", "#<rank> - <name> (<kills>)"); }
		if (rank >= 4) { msg = plugin.config.getString("messages.other_rank", "#<rank> - <name> (<kills>)"); }
		if (name.equalsIgnoreCase(nm)) {
			msg = plugin.config.getString("messages.own_rank", "---#<rank> - <name> (<kills>)---");
		} 
		msg = msg.replaceAll("<rank>", "" + rank);
		msg = msg.replaceAll("<name>", "" + name);
		msg = msg.replaceAll("<field>", "" + key);
		msg = msg.replaceAll("<kills>", "" + kills);
		msg = msg.replaceAll("&([0-9A-F])", "\u00A7$1");
		sender.sendMessage(msg);
	}
	
	// header message
	private void playMessage(CommandSender sender, String key, String field, int loops) {
		String msg = plugin.config.getString("messages.header", "Leaders -Top <num>- (<field>)");
		msg = msg.replaceAll("<field>", field);
		msg = msg.replaceAll("<num>", "" + loops);
		msg = msg.replaceAll("&([0-9A-F])", "\u00A7$1");
		sender.sendMessage(msg);
	}
	
	public void deleteYaml(String name) {
		if (plugin.users.getKeys(name) != null) {
			plugin.users.removeProperty(name);
		}
	}

	public void deleteSqlite(String name) {
		try {
			String query1 = "SELECT * FROM `users` WHERE `name` = '" + name + "';";
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/DeathCounter/users.db");
			Statement state = conn.createStatement();
			ResultSet rs = state.executeQuery(query1);
			while (rs.next()) {
				String query2 = "DELETE FROM `users` WHERE `name = '" + name + "';";
				int rs1 = state.executeUpdate(query2);
				if (plugin.config.getInt("settings.log_verbosity", 1) >= 2 && rs1 == 1) {
					log.info("player " + name + " has been deleted");
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

	public void deleteMysql(String string) {
		// TODO Auto-generated method stub
		
	}

}
