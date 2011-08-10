package net.krinsoft.deathcounter.types;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Map;

import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.util.DeathLogger;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

public class Leaderboards {

	public DeathCounter plugin;
	public DeathLogger log;

	public Leaderboards(DeathCounter instance) {
		plugin = instance;
		log = plugin.log;
	}

	public void fetch(CommandSender sender, String field, int loops) {
		if (field.matches("@.+")) {
			playMessage(sender, getHeaderStats(field));
		} else {
			playMessage(sender, getHeaderRanks(field, loops));
		}

		String storage = plugin.config.getString("settings.storage.type", "yaml");

		if (storage.equalsIgnoreCase("yaml")) {
			fetchYaml(sender, field, loops);
		} else if (storage.equalsIgnoreCase("sqlite")) {
			fetchSqlite(sender, field, loops);
		} else if (storage.equalsIgnoreCase("mysql")) {
			fetchMysql(sender, field, loops);
		}
	}

	private void fetchYaml(CommandSender sender, String field, int loops) {
		if (field.equalsIgnoreCase("total") || plugin.monsters.contains(field)) {
			LinkedList<String> names = new LinkedList<String>();
			LinkedList<Integer> counts = new LinkedList<Integer>();

			int minCount = 0;
			for (String key : plugin.users.getKeys()) {
				int count = plugin.users.getInt(key + "." + field, 0);

				// has kills and (more kills than the lowest or there's room for a tie) 
				if (count > 0 && (count > minCount || (count == minCount && counts.size() < loops))) {
					int index;
					for (index = 0; index < counts.size(); index++) {
						if (counts.get(index) < count) {
							break;
						}
					}

					counts.add(index, count);
					names.add(index, key);

					if (counts.size() > loops) {
						minCount = counts.removeLast();
						names.removeLast();
					}
				}
			}

			for (int i = 0; i < counts.size(); i++) {
				playMessage(sender, getMessageRanks(sender, i + 1, names.get(i), field, counts.get(i)));
			}
		} else {
			String name = field.substring(1);

			ConfigurationNode node = plugin.users.getNode(name);
			if (node == null) {
				// no user found
				sender.sendMessage("no stats found");
				return;
			}

			String msg = null;

			for (Map.Entry<String, Object> entry : node.getAll().entrySet()) {
				msg = buildMessageStats(msg, entry.getKey(), entry.getValue().toString());
			}

			playMessage(sender, msg);
		}
	}

	private void fetchSqlite(CommandSender sender, String field, int loops) {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/DeathCounter/users.db");
			Statement state = conn.createStatement();
			ResultSet rs;
			String column = "";

			if (field.equalsIgnoreCase("total") || plugin.monsters.contains(field)) {
				if (field.equalsIgnoreCase("total")) {
					for (String mob : plugin.monsters) {
						column += " + `" + mob + "`";
					}

					column = "(" + column.substring(3) + ")";
				} else {
					column = "`" + field + "`";
				}

				rs = state.executeQuery("SELECT `name`, " + column + " AS '" + field + "' FROM `users` ORDER BY `" + field + "` DESC LIMIT " + loops + ";");

				while (rs.next()) {
					playMessage(sender, getMessageRanks(sender, rs.getRow(), rs.getString("name"), field, rs.getInt(field)));
				}
			} else {
				for (String mob : plugin.monsters) {
					column += ", `" + mob + "`";
				}
				rs = state.executeQuery("SELECT `name`" + column + " FROM `users` WHERE `name` = '" + field.substring(1) + "';");

				String msg = null;
				if (rs.next()) {
					for (String mob : plugin.monsters) {
						msg = buildMessageStats(msg, mob, rs.getString(mob));
					}
				}

				playMessage(sender, msg);
			}

			state.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void fetchMysql(CommandSender sender, String field, int loops) {
		sender.sendMessage("");
	}

	private void playMessage(CommandSender sender, String msg) {
		String[] messages = msg.replaceAll("(?i)&([0-F])", "\u00A7$1").split("<newline>");
		for (String message : messages) {
			sender.sendMessage(message);
		}
	}

	// build normal ranks message
	private String getMessageRanks(CommandSender sender, int rank, String name, String field, int count) {
		String playerName = sender instanceof Player ? ((Player) sender).getName() : "";

		String msg;
		if (playerName.equalsIgnoreCase(name)) {
			msg = plugin.config.getString("messages.own_rank", "---#<rank> - <name> (<kills>)---");
		} else {
			String ordinal = "other";
			if (rank == 1) {
				ordinal = "first";
			} else if (rank == 2) {
				ordinal = "second";
			} else if (rank == 3) {
				ordinal = "third";
			}

			msg = plugin.config.getString("messages." + ordinal + "_rank", "#<rank> - <name> (<kills>)");
		}

		msg = msg.replaceAll("<rank>", Integer.toString(rank));
		msg = msg.replaceAll("<name>", name);
		msg = msg.replaceAll("<field>", field);
		msg = msg.replaceAll("<kills>", Integer.toString(count));
		return msg;
	}

	// build stats message
	private String buildMessageStats(String msg, String key, String kills) {
		if (msg == null) {
			msg = plugin.config.getString("messages.stats", "animals -- pig:<pig> squid:<squid> chicken:<chicken> cow:<cow> sheep:<sheep> slime:<slime> wolf:<wolf><newline>monsters -- skeleton:<skeleton> creeper:<creeper> zombie:<zombie> spider:<spider> pigzombie:<pigzombie> ghast:<ghast> player:<player><newline>total kills -- <total>");
		}

		return msg.replaceAll("<" + key + ">", kills);
	}

	// ranks header message
	private String getHeaderRanks(String field, int loops) {
		String msg = plugin.config.getString("messages.header", "Leaders -Top <num>- (<field>)");
		msg = msg.replaceAll("<field>", field);
		return msg.replaceAll("<num>", Integer.toString(loops));
	}

	// stats header message
	private String getHeaderStats(String name) {
		String msg = plugin.config.getString("messages.header_stats", "Stats - <name>");
		return msg.replaceAll("<name>", name);
	}

	public void delete(String name) {
		String storage = plugin.config.getString("settings.storage.type", "yaml");

		if (storage.equalsIgnoreCase("yaml")) {
			deleteYaml(name);
		} else if (storage.equalsIgnoreCase("sqlite")) {
			deleteSqlite(name);
		} else if (storage.equalsIgnoreCase("mysql")) {
			deleteMysql(name);
		}
	}

	private void deleteYaml(String name) {
		if (plugin.users.getKeys(name) != null) {
			plugin.users.removeProperty(name);
		}
	}

	private void deleteSqlite(String name) {
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

	private void deleteMysql(String string) {
		// TODO Auto-generated method stub
	}
}
