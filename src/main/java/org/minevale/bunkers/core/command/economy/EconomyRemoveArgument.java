package org.minevale.bunkers.core.command.economy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.api.exception.InsufficientFundsException;
import org.minevale.bunkers.core.command.CommandArgument;
import org.minevale.bunkers.core.player.PlayerData;
import org.minevale.bunkers.core.player.currencies.CurrencyType;

public class EconomyRemoveArgument implements CommandArgument {

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

        CurrencyType currency = CurrencyType.parse(args[2]);
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
            BunkersCore.getInstance().getApi().removeCurrency(playerData, currency, amount);
        } catch (InsufficientFundsException e) {
            sender.sendMessage(ChatColor.RED + e.getFriendlyMessage());
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Removed " + amount + " of " + currency.getFriendlyName() + " from " + ChatColor.WHITE + playerData.getUsername() + ChatColor.GREEN + ".");

    }

    @Override
    public String usage() {
        return "remove <player> <currency> <amount>";
    }
}
