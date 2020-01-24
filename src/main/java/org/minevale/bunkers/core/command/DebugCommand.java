package org.minevale.bunkers.core.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DebugCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /debug balance");
            return true;
        }

        if (args[0].equalsIgnoreCase("balance")) {

        } else {
            sender.sendMessage(ChatColor.RED + "Sub-command " + label + " " + args[0] + " not found.");
        }

        return true;
    }
}
