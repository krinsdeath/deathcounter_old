package net.krinsoft.deathcounter.types;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;
import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.util.DeathLogger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

public class Leaderboards {

    public DeathCounter plugin;
    public DeathLogger log;

    public Leaderboards(DeathCounter instance) {
        this.plugin = instance;
        this.log = this.plugin.log;
    }

    public void fetch(CommandSender sender, String field, int loops) {
        if (field.matches("@.+")) {
            playMessage(sender, getHeaderStats(field));
        } else {
            playMessage(sender, getHeaderRanks(field, loops));
        }

        String storage = this.plugin.config.getString("settings.storage.type", "yaml");

        if (storage.equalsIgnoreCase("yaml")) {
            fetchYaml(sender, field, loops);
        } else if (storage.equalsIgnoreCase("sqlite")) {
            fetchSqlite(sender, field, loops);
        } else if (storage.equalsIgnoreCase("mysql")) {
            fetchMysql(sender, field, loops);
        }
    }

    private void fetchYaml(CommandSender sender, String field, int loops) {
        if ((field.equalsIgnoreCase("total")) || (this.plugin.monsters.contains(field))) {
            LinkedList<String> names = new LinkedList();
            LinkedList<Integer> counts = new LinkedList();

            int minCount = 0;
            for (String key : this.plugin.users.getKeys()) {
                int count = this.plugin.users.getInt(key + "." + field, 0);

                if ((count > 0) && ((count > minCount) || ((count == minCount) && (counts.size() < loops)))) {
                    int index;
                    for (index = 0; index < counts.size(); index++) {
                        if (counts.get(index) < count) {
                            break;
                        }
                    }
                    counts.add(index, Integer.valueOf(count));
                    names.add(index, key);

                    if (counts.size() > loops) {
                        minCount = ((Integer) counts.removeLast()).intValue();
                        names.removeLast();
                    }
                }
            }

            for (int i = 0; i < counts.size(); i++) {
                playMessage(sender, getMessageRanks(sender, i + 1, (String) names.get(i), field, ((Integer) counts.get(i)).intValue()));
            }
        } else {
            String name = Pattern.compile(".+((?i)" + field.substring(1) + ").+").matcher(this.plugin.users.getKeys().toString()).replaceAll("$1");
            ConfigurationNode node = this.plugin.users.getNode(name);
            if (node == null) {
                sender.sendMessage("no stats found");
                return;
            }

            String msg = null;

            for (Map.Entry entry : node.getAll().entrySet()) {
                msg = buildMessageStats(msg, (String) entry.getKey(), entry.getValue().toString());
            }

            playMessage(sender, msg);
        }
    }

    private void fetchSqlite(CommandSender sender, String field, int loops) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/DeathCounter/users.db");
            Statement state = conn.createStatement();

            String column = "";
            ResultSet rs;
            if ((field.equalsIgnoreCase("total")) || (this.plugin.monsters.contains(field))) {
                if (field.equalsIgnoreCase("total")) {
                    for (String mob : this.plugin.monsters) {
                        column = column + " + `" + mob + "`";
                    }
                    column = "(" + column.substring(3) + ")";
                } else {
                    column = "`" + field + "`";
                }
                rs = state.executeQuery("SELECT `name`, " + column + " AS '" + field + "' FROM `users` ORDER BY `" + field + "` DESC LIMIT " + loops + ";");
                while (rs.next()) {
                    playMessage(sender, getMessageRanks(sender, rs.getRow(), rs.getString("name"), field, rs.getInt(field)));
                    continue;
                }
            } else {
                String name = field.substring(1).replaceAll("[\\.\\\\/\\'\"]", "");
                for (String mob : this.plugin.monsters) {
                    column = column + ", `" + mob + "`";
                }
                rs = state.executeQuery("SELECT `name`" + column + " FROM `users` WHERE `name` LIKE '" + name + "';");
                String msg = null;
                if (rs.next()) {
                    for (String mob : this.plugin.monsters) {
                        msg = buildMessageStats(msg, mob, rs.getString(mob));
                    }
                }

                playMessage(sender, msg);
            }

            state.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            this.log.warn("SQLite JDBC Driver not found. Defaulting to YAML.");
            this.log.warn("Error: " + e);
            this.plugin.config.setProperty("settings.storage.type", "yaml");
        } catch (SQLException e) {
            this.log.warn("Error: " + e);
        }
    }

    private void fetchMysql(CommandSender sender, String field, int loops) {
        sender.sendMessage("");
    }

    private void playMessage(CommandSender sender, String msg) {
        String[] messages = msg.replaceAll("(?i)&([0-F])", "§$1").split("<newline>");
        for (String message : messages) {
            sender.sendMessage(message);
        }
    }

    private String getMessageRanks(CommandSender sender, int rank, String name, String field, int count) {
        String playerName = sender.getName();
        String msg;
        if (playerName.equalsIgnoreCase(name)) {
            msg = this.plugin.config.getString("messages.own_rank", "---#<rank> - <name> (<kills>)---");
        } else {
            String ordinal = "other";
            if (rank == 1) {
                ordinal = "first";
            } else if (rank == 2) {
                ordinal = "second";
            } else if (rank == 3) {
                ordinal = "third";
            }

            msg = this.plugin.config.getString("messages." + ordinal + "._rank", "#<rank> - <name> (<kills>)");
        }

        msg = msg.replaceAll("<rank>", Integer.toString(rank));
        msg = msg.replaceAll("<name>", name);
        msg = msg.replaceAll("<field>", field);
        msg = msg.replaceAll("<kills>", Integer.toString(count));
        return msg;
    }

    private String buildMessageStats(String msg, String key, String kills) {
        if (msg == null) {
            msg = this.plugin.config.getString("messages.stats", ChatColor.GREEN + "Animals " + ChatColor.WHITE + "-- Pigs: <pig>, Squid: <squid>, Chickens: <chicken>, Cows: <cow>, Sheep: <sheep><newline>" + "Slime: <slime>, Wolves: <wolf><newline>" + ChatColor.RED + "Monsters " + ChatColor.WHITE + "-- Skeletons: <skeleton>, Creepers: <creeper>, Zombies: <zombie>, Spiders: <spider><newline>" + "PigZombies: <pigzombie>, Ghasts: <ghast>, Endermen: <enderman>, CaveSpiders: <cavespider><newline>" + "Silverfish: <silverfish>, " + ChatColor.AQUA + "Players" + ChatColor.WHITE + ": <player><newline>");
        }

        return msg.replaceAll("<" + key + ">", kills);
    }

    private String getHeaderRanks(String field, int loops) {
        String msg = this.plugin.config.getString("messages.header", "Leaders -Top <num>- (<field>)");
        msg = msg.replaceAll("<field>", field);
        return msg.replaceAll("<num>", Integer.toString(loops));
    }

    private String getHeaderStats(String name) {
        String msg = this.plugin.config.getString("messages.header_stats", "Stats - <name>");
        return msg.replaceAll("<name>", name);
    }

    public void delete(String name) {
        String storage = this.plugin.config.getString("settings.storage.type", "yaml");

        if (storage.equalsIgnoreCase("yaml")) {
            deleteYaml(name);
        } else if (storage.equalsIgnoreCase("sqlite")) {
            deleteSqlite(name);
        } else if (storage.equalsIgnoreCase("mysql")) {
            deleteMysql(name);
        }
    }

    private void deleteYaml(String name) {
        if (this.plugin.users.getKeys(name) != null) {
            this.plugin.users.removeProperty(name);
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
                if ((this.plugin.config.getInt("settings.log_verbosity", 1) >= 2) && (rs1 == 1)) {
                    this.log.info("player " + name + " has been deleted");
                }
            }
            state.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            this.log.warn("SQLite JDBC Driver not found. Defaulting to YAML.");
            this.log.warn("Error: " + e);
            this.plugin.config.setProperty("settings.storage.type", "yaml");
        } catch (SQLException e) {
            this.log.warn("Error: " + e);
        }
    }

    private void deleteMysql(String string) {
    }
}
