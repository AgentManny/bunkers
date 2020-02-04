package org.minevale.bunkers.core.command.economy;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.command.CommandArgument;
import org.minevale.bunkers.core.player.PlayerData;
import org.minevale.bunkers.core.player.balance.Balance;

public class EconomyBalanceArgument implements CommandArgument {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /economy " + usage());
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player " + args[1] + " not found.");
            return;
        }

        PlayerData playerData = BunkersCore.getInstance().getPlayerDataManager().getPlayerData(target);
        int totalBalance = 0;
        for (Balance balance : Balance.values()) {
            sender.sendMessage(ChatColor.GREEN + balance.getFriendlyName() + ": " + ChatColor.WHITE + playerData.getBalance(balance));
        }
        sender.sendMessage(ChatColor.GREEN + "- - Total balance: " + playerData.getBalance() + " - -");
    }

    @Override
    public String usage() {
        return "balance <player>";
    }
}
