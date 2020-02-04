package org.minevale.bunkers.core.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.minevale.bunkers.core.chat.ChatType;
import org.minevale.bunkers.core.command.CommandArgument;

public class ChatClearArgument implements CommandArgument {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /chat " + usage());
            return;
        }

        ChatType type = ChatType.parse(args[2]);
        if (type == null) {
            sender.sendMessage(ChatColor.RED + "Chat " + args[2] + " not found.");
            return;
        }

        for (int i = 0; i < 100; i++) {
            sender.sendMessage(" ");
        }
        sender.sendMessage(ChatColor.GREEN + "Cleared " + type.name().toLowerCase() + " chat");
    }

    @Override
    public String usage() {
        return "clear <global|local> [distance]";
    }

    @Override
    public String permission() {
        return "minevale.clearchat";
    }
}
