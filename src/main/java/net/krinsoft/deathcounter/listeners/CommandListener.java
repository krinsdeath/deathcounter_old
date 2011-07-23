package net.krinsoft.deathcounter.listeners;

import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.types.DeathPlayer;
import net.krinsoft.deathcounter.util.DeathLogger;

import org.bukkit.ChatColor;
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

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("deathcount")) {
			if (sender instanceof Player) {
				if (plugin.players.get((Player) sender) == null) {
					Player p = (Player) sender;
					plugin.players.put(p, new DeathPlayer(plugin, p.getName()));
				}
			}
			if (sender.hasPermission("deathcounter.users")) {
				int loops = plugin.config.getInt("settings.leaderboards", 5);
				if (args.length >= 2) {
					loops = Integer.parseInt(args[1]);
				}
				if (loops == 0) { return false; }
				loops = (loops <= 10) ? loops : 10;
				if (args.length == 0) {
					if (plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("yaml")) {
						plugin.leaders.fetchYaml(sender, "leaders", loops);
						return true;
					} else if (plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("sqlite")) {
						plugin.leaders.fetchSqlite(sender, "leaders", loops);
						return true;
					} else if (plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("mysql")) {
						plugin.leaders.fetchMysql(sender, "leaders", loops);
						return true;
					} else {
						log.warn(ChatColor.RED + "What storage type are you using?");
						return true;
					}
				} else if (args.length >= 1) {
					if (args[0].equalsIgnoreCase("leaders")) {
						// Set up the maximum loops, and make sure the player doesn't specify something too high
						if (plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("yaml")) {
							plugin.leaders.fetchYaml(sender, "leaders", loops);
							return true;
						} else if (plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("sqlite")) {
							plugin.leaders.fetchSqlite(sender, "leaders", loops);
							return true;
						} else if (plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("mysql")) {
							plugin.leaders.fetchMysql(sender, "leaders", loops);
							return true;
						} else {
							log.warn(ChatColor.RED + "What storage type are you using?");
							return true;
						}
					} else if (plugin.monsters.contains(args[0].toLowerCase())) {
						// Set up the maximum loops, and make sure the player doesn't specify something too high
						if (plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("yaml")) {
							plugin.leaders.fetchYaml(sender, args[0].toLowerCase(), loops);
							return true;
						} else if (plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("sqlite")) {
							plugin.leaders.fetchSqlite(sender, args[0].toLowerCase(), loops);
							return true;
						} else if (plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("mysql")) {
							plugin.leaders.fetchMysql(sender, args[0].toLowerCase(), loops);
							return true;
						} else {
							log.warn(ChatColor.RED + "What storage type are you using?");
							return true;
						}
					} else if (args[0].equals("reset")) {
						if (args.length >= 2) {
							// Check if permissions exist for this user
							if (sender.hasPermission("deathcounter.admins")) {
								if (plugin.getServer().getPlayer(args[1]) != null) {
									Player player = plugin.getServer().getPlayer(args[1]);
									if (player.getName().equalsIgnoreCase(args[1])) {
										if (plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("yaml")) {
											plugin.leaders.deleteYaml(args[1]);
											return true;
										} else if (plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("sqlite")) {
											plugin.leaders.deleteSqlite(args[1]);
											return true;
										} else if (plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("mysql")) {
											plugin.leaders.deleteMysql(args[1]);
											return true;
										} else {
											log.warn(ChatColor.RED + "What storage type are you using?");
											return true;
										}
									} else {
										sender.sendMessage(ChatColor.RED + "You must enter the player's name exactly.");
										return true;
									}
								} else {
									log.warn("Player " + args[1] + " not found.");
									return true;
								}
							} else {
								// Player doesn't have permission for 'reset'
								sender.sendMessage(ChatColor.RED + "You do not have permission to do that.");
								return true;
							}
						} else {
							// Invalid number of arguments
							sender.sendMessage(ChatColor.RED + "Invalid number of arguments.");
							return true;
						}
					} else {
						// The specified argument had no handler
						sender.sendMessage(ChatColor.RED + "An option for " + args[0] + " could not be found.");
						return true;
					}
				} else {
					// Invalid number of arguments
					sender.sendMessage(ChatColor.RED + "Invalid number of arguments.");
					return true;
				}
			} else {
				// Player has no permissions
				sender.sendMessage(ChatColor.RED + "You do not have permission to do that.");
				return true;
			}
		}
		return true;
	}

}
