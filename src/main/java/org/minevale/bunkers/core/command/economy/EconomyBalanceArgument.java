package org.minevale.bunkers.core.command.economy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
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

        PlayerData playerData = BunkersCore.getInstance().getPlayerDataManager().getPlayerData(args[1]);
        if (playerData == null) {
            sender.sendMessage(ChatColor.RED + "Player " + args[1] + " not found.");
            return;
        }

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
