package org.minevale.bunkers.core.command.economy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.command.CommandArgument;
import org.minevale.bunkers.core.player.PlayerData;
import org.minevale.bunkers.core.player.currencies.CurrencyType;

public class EconomyClearArgument implements CommandArgument {

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

        CurrencyType currency = args.length < 3 ? null : CurrencyType.parse(args[2]);

        int removedAmount;
        if (currency == null) {
            removedAmount = BunkersCore.getInstance().getApi().clearBalance(playerData);
        } else {
            removedAmount = BunkersCore.getInstance().getApi().clearBalance(playerData, currency);
        }

        sender.sendMessage(ChatColor.GREEN + "Removed " + removedAmount + " of " + (currency == null ? "all currency" : currency.getFriendlyName()) + " from " + ChatColor.WHITE + playerData.getUsername() + ChatColor.GREEN + ".");
    }

    @Override
    public String usage() {
        return "clear <player> [currency]";
    }
}
