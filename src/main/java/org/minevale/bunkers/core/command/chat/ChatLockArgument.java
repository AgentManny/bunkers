package org.minevale.bunkers.core.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.minevale.bunkers.core.command.CommandArgument;

public class ChatLockArgument implements CommandArgument {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /chat " + usage());
            return;
        }
        sender.sendMessage("Works!");
    }

    @Override
    public String usage() {
        return "<global|local>";
    }
}
