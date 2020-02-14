package org.minevale.bunkers.core.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.chat.ChatType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("minevale.clearchat") || sender.hasPermission("minevale.chatlock")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Usage: /chat <lock|clear> [global|local]");
                return true;
            }

            if (args[0].equalsIgnoreCase("clear")) {
                if (!sender.hasPermission("minevale.clearchat")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to clear chat.");
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /chat clear <local|global> [radius]");
                    return true;
                }

                ChatType type;
                List<Player> players;
                if (args[1].equalsIgnoreCase("local")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
                        return true;
                    }

                    type = ChatType.LOCAL;
                    Player player = (Player) sender;

                    int radius = BunkersCore.getInstance().getConfig().getInt("chat.clear-radius", 25);
                    if (args.length > 3) {
                        try {
                            radius = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "Value " + args[2] + " isn't valid.");
                            return true;
                        }
                    }

                    players = player.getNearbyEntities(radius, radius, radius)
                            .stream()
                            .filter(entity -> entity instanceof Player)
                            .map(entity -> (Player)entity)
                            .collect(Collectors.toList());


                } else if (args[1].equalsIgnoreCase("global")) {
                    if (!sender.hasPermission("minevale.clearchat.global")) {
                        sender.sendMessage(ChatColor.RED + "You don't have permission to clear global chat.");
                        return true;
                    }

                    type = ChatType.GLOBAL;
                    players = new ArrayList<>(BunkersCore.getInstance().getServer().getOnlinePlayers());
                } else {
                    sender.sendMessage(ChatColor.RED + "Chat mode " + args[1] + " not found.");
                    return true;
                }

                List<String> friendlyMessage = new ArrayList<>();

                for (Player other : players) {
                    if (!other.hasPermission("minevale.clearchat.bypass")) {
                        for (int i = 0; i < 100; i++) {
                            other.sendMessage(" ");
                        }
                        friendlyMessage.add(other.getName());
                    }
                }

                sender.sendMessage(ChatColor.GREEN + "Cleared " + ChatColor.WHITE + players.size() + ChatColor.GREEN + " player(s) chat.");
                if (type == ChatType.LOCAL) {
                    sender.sendMessage(ChatColor.GREEN + "Players cleared: " + ChatColor.WHITE + Strings.join(friendlyMessage, ", "));
                }

            } else if (args[0].equalsIgnoreCase("lock")) {
                if (!sender.hasPermission("minevale.chatlock")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to toggle chat.");
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /chat lock global");
                    return true;
                }

                if (args[1].equalsIgnoreCase("global")) {
                    boolean newValue = !BunkersCore.getInstance().isChatLock();
                    BunkersCore.getInstance().setChatLock(newValue);

                    sender.sendMessage((newValue ? ChatColor.RED : ChatColor.GREEN) + "You have " + (newValue ? "locked" : "unlocked") + " global chat.");
                } else {
                    sender.sendMessage(ChatColor.RED + "You can only lock global chat.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Command argument " + args[0] + " not found.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
        }


        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /chat <lock|clear> [global|local]");
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
