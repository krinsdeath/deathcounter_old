package net.krinsoft.deathcounter.listeners;

import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.types.DeathPlayer;
import net.krinsoft.deathcounter.util.DeathLogger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandListener
        implements CommandExecutor {

    public DeathCounter plugin;
    public DeathLogger log;

    public CommandListener(DeathCounter instance) {
        this.plugin = instance;
        this.log = this.plugin.log;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("deathcounter.users")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to do that.");
            return true;
        }

        if (((sender instanceof Player))
                && (this.plugin.players.get(sender.getName()) == null)) {
            this.plugin.players.put(sender.getName(), new DeathPlayer(this.plugin, sender.getName()));
        }

        int loops = Math.min((args.length >= 2) && (args[1].matches("[1-9](?:[0-9]+)?")) ? Integer.parseInt(args[1]) : this.plugin.config.getInt("settings.leaderboards", 5), 10);
        if (loops < 1) {
            loops = 1;
        }

        String field = "total";

        if ((args.length > 0) && (!args[0].equalsIgnoreCase("leaders"))) {
            if (args[0].equalsIgnoreCase("reset")) {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Invalid number of arguments.");
                } else if (!sender.hasPermission("deathcounter.admins")) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to do that.");
                } else {
                    Player player = this.plugin.getServer().getPlayer(args[1]);
                    if ((player == null) || (!player.getName().equalsIgnoreCase(args[1]))) {
                        sender.sendMessage(ChatColor.RED + "Player " + args[1] + " was not found. You must enter an online player's name exactly.");
                    } else {
                        this.plugin.leaders.delete(player.getName());
                        sender.sendMessage(ChatColor.GREEN + "Player " + player.getName() + "'s counter was reset.");
                    }

                }

                return true;
            }

            if ((!args[0].matches("@.+")) && (!this.plugin.monsters.contains(args[0].toLowerCase()))) {
                sender.sendMessage(ChatColor.RED + "An option for " + args[0] + " could not be found.");
                return true;
            }

            field = args[0];
        }

        this.plugin.leaders.fetch(sender, field.toLowerCase(), loops);
        return true;
    }
}
