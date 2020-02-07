package org.minevale.bunkers.core.command.economy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.api.exception.InventoryFullException;
import org.minevale.bunkers.core.command.CommandArgument;
import org.minevale.bunkers.core.player.PlayerData;
import org.minevale.bunkers.core.player.balance.Balance;

public class EconomyAddArgument implements CommandArgument {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: /economy " + usage());
            return;
        }

        PlayerData playerData = BunkersCore.getInstance().getPlayerDataManager().getPlayerData(args[1]);
        if (playerData == null) {
            sender.sendMessage(ChatColor.RED + "Player " + args[1] + " not found.");
            return;
        }

        Balance currency = Balance.parse(args[2]);
        if (currency == null) {
            sender.sendMessage(ChatColor.RED + "Currency " + args[2] + " not found.");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Value " + args[3] + " not found.");
            return;
        }


        try {
            BunkersCore.getInstance().getApi().addCurrency(playerData, currency, amount);
        } catch (InventoryFullException e) {
            sender.sendMessage(ChatColor.RED + e.getFriendlyMessage());
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Added " + amount + " of " + currency.getFriendlyName() + " to " + ChatColor.WHITE + playerData.getUsername() + ChatColor.GREEN + ".");

    }

    @Override
    public String usage() {
        return "add <player> <currency> <amount>";
    }
}
