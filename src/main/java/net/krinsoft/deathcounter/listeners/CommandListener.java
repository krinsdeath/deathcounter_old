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

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("deathcounter.users")) {
			// Player has no permissions
			sender.sendMessage(ChatColor.RED + "You do not have permission to do that.");
			return true;
		}

		if (sender instanceof Player) {
			if (plugin.players.get((Player) sender) == null) {
				Player p = (Player) sender;
				plugin.players.put(p, new DeathPlayer(plugin, p.getName()));
			}
		}

		// Set up the maximum loops, and make sure the player doesn't specify something too high
		int loops = Math.min(args.length >= 2 && args[1].matches("[1-9](?:[0-9]+)?") ? Integer.parseInt(args[1]) : plugin.config.getInt("settings.leaderboards", 5), 10);
		if (loops < 1) {
			loops = 1;
		}

		// default request if no args or 1st arg is 'leaders'
		String field = "total";

		if (args.length > 0 && !args[0].equalsIgnoreCase("leaders")) {
			if (args[0].equalsIgnoreCase("reset")) {
				if (args.length < 2) {
					// Invalid number of arguments
					sender.sendMessage(ChatColor.RED + "Invalid number of arguments.");

				} else if (!sender.hasPermission("deathcounter.admins")) {
					// Player doesn't have permission for 'reset'
					sender.sendMessage(ChatColor.RED + "You do not have permission to do that.");

				} else {
					Player player = plugin.getServer().getPlayer(args[1]);
					if (player == null || !player.getName().equalsIgnoreCase(args[1])) {
						// unable to find exact player name online	
						sender.sendMessage(ChatColor.RED + "Player " + args[1] + " was not found. You must enter an online player's name exactly.");

					} else {
						// found player, let's delete him
						plugin.leaders.delete(player.getName());
						sender.sendMessage(ChatColor.GREEN + "Player " + player.getName() + "'s counter was reset.");

					}

				}
				return true;

			}
			if (!args[0].matches("@.+") && !plugin.monsters.contains(args[0].toLowerCase())) {
				// The specified argument had no handler
				sender.sendMessage(ChatColor.RED + "An option for " + args[0] + " could not be found.");
				return true;
			}
			// got a monster!
			field = args[0];
		}
		// let's find the stats!
		plugin.leaders.fetch(sender, field, loops);
		return true;
	}
}
