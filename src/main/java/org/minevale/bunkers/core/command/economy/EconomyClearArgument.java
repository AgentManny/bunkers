package org.minevale.bunkers.core.command.economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.command.CommandArgument;
import org.minevale.bunkers.core.player.PlayerData;
import org.minevale.bunkers.core.player.balance.Balance;

public class EconomyClearArgument implements CommandArgument {

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
        Balance currency = args.length < 3 ? null : Balance.parse(args[2]);

        int removedAmount;
        if (currency == null) {
            removedAmount = BunkersCore.getInstance().getApi().clearBalance(target);
        } else {
            removedAmount = BunkersCore.getInstance().getApi().clearBalance(target, currency);
        }

        sender.sendMessage(ChatColor.GREEN + "Removed " + removedAmount + " of " + (currency == null ? "all currency" : currency.getFriendlyName()) + " from " + ChatColor.WHITE + target.getName() + ChatColor.GREEN + ".");
    }

    @Override
    public String usage() {
        return "clear <player> [currency]";
    }
}
