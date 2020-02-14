package org.minevale.bunkers.core.command.bunker;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.command.CommandArgument;
import org.minevale.bunkers.core.player.PlayerData;

public class BunkerJoinArgument implements CommandArgument {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /bunker " + usage());
            return;
        }

        Player player = (Player) sender;
        PlayerData playerData = BunkersCore.getInstance().getPlayerDataManager().getPlayerData(args[1]);
        if (playerData == null) {
            sender.sendMessage(ChatColor.RED + "Player " + args[1] + " not found.");
            return;
        }

        if (playerData.getPlayerBunker() == null) {
            sender.sendMessage(ChatColor.RED + "Player does not have a bunker.");
            return;
        }

        playerData.getPlayerBunker().join(player);
    }

    @Override
    public String usage() {
        return "join <player>";
    }
}