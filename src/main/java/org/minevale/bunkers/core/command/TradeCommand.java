package org.minevale.bunkers.core.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.minevale.bunkers.core.BunkersCore;

public class TradeCommand implements CommandExecutor {


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /trade <player>");
            return true;
        }

        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player " + args[0] + " not found.");
            return true;
        }

        if (player == target) {
            sender.sendMessage(ChatColor.RED + "You can't trade with yourself.");
            return true;
        }

        if (BunkersCore.getInstance().getTradeManager().getTradeRequest(player) != null) {
            sender.sendMessage(ChatColor.RED + "You already have a pending trade request.");
            return true;
        }

        int tradeDistance = BunkersCore.getInstance().getConfig().getInt("trade.distance", 10);
        if (target.getLocation().distance(player.getLocation()) > 10) {
            sender.sendMessage(ChatColor.RED + "You need to be within " + tradeDistance + " blocks to trade with this player.");
            return true;
        }


        BunkersCore.getInstance().getTradeManager().createTradeRequest(player, target);
        return true;
    }
}
