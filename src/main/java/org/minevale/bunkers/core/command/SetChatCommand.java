package org.minevale.bunkers.core.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.chat.ChatType;

public class SetChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("minevale.setchat")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /setchat <local|global>");
            return true;
        }

        ChatType type = ChatType.parse(args[0]);
        if (type == null) {
            sender.sendMessage(ChatColor.RED + "Chat mode " + args[0] + " not found.");
            return true;
        }

        BunkersCore.getInstance().setChatMode(type);
        sender.sendMessage(ChatColor.GREEN + "Set global chat type to " + ChatColor.WHITE + type.name() + ChatColor.GREEN + ".");
        return true;
    }

}
