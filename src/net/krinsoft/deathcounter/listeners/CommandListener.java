package net.krinsoft.deathcounter.listeners;

import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.types.DeathPlayer;
import net.krinsoft.deathcounter.util.DeathLogger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandListener implements CommandExecutor {
	public DeathCounter plugin;
	public DeathLogger log;
	
	public CommandListener(DeathCounter instance) {
		plugin = instance;
		log = plugin.log;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			if (plugin.players.get((Player) sender) == null) {
				Player p = (Player) sender;
				plugin.players.put(p, new DeathPlayer(plugin, p.getName()));
			}
		}
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("leaders")) {
				// Set up the maximum loops, and make sure the player doesn't specify something too high
				int loops = plugin.config.getInt("settings.leaderboards", 5);
				if (args.length >= 2) {
					loops = Integer.parseInt(args[1]);
				}
				if (loops == 0) { return false; }
				loops = (loops <= 10) ? loops : 10;
				if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("yaml")) {
					plugin.leaders.fetchYaml(sender, "leaders", loops);
					return true;
				} else if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("sqlite")) {
					plugin.leaders.fetchSqlite(sender, "leaders", loops);
					return true;
				} else if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("mysql")) {
					plugin.leaders.fetchMysql(sender, "leaders", loops);
					return true;
				}
			} else if (plugin.monsters.contains(args[0].toLowerCase())) {
				// Set up the maximum loops, and make sure the player doesn't specify something too high
				int loops = plugin.config.getInt("settings.leaderboards", 5);
				if (args.length >= 2) {
					loops = Integer.parseInt(args[1]);
				}
				if (loops == 0) { return false; }
				loops = (loops <= 10) ? loops : 10;
				if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("yaml")) {
					plugin.leaders.fetchYaml(sender, args[0].toLowerCase(), loops);
					return true;
				} else if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("sqlite")) {
					plugin.leaders.fetchSqlite(sender, args[0].toLowerCase(), loops);
					return true;
				} else if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("mysql")) {
					plugin.leaders.fetchMysql(sender, args[0].toLowerCase(), loops);
					return true;
				}
			} else if (args[0].equals("reset")) {
				if (args.length >= 2) {
					if (!(sender instanceof Player)) { return false; }
					boolean hasPermission = false;
					if (plugin.perm) {
						if (plugin.permissions.has((Player) sender, "deathcounter.admin")) {
							hasPermission = true;
						}
					}
					if (sender.isOp()) {
						hasPermission = true;
					}
					if (hasPermission) {
						if (plugin.getServer().getPlayer(args[1]) != null) {
							log.info("found player " + args[1]);
							if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("yaml")) {
								plugin.leaders.deleteYaml(args[1]);
								return true;
							} else if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("sqlite")) {
								plugin.leaders.deleteSqlite(args[1]);
								return true;
							} else if (plugin.config.getString("settings.storage.type").equalsIgnoreCase("mysql")) {
								plugin.leaders.deleteMysql(args[1]);
								return true;
							}
						} else {
							return false;
						}
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
		return false;
	}

}
