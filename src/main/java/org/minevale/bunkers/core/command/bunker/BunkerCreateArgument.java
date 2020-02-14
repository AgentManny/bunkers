package org.minevale.bunkers.core.command.bunker;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.command.CommandArgument;
import org.minevale.bunkers.core.player.PlayerData;

public class BunkerCreateArgument implements CommandArgument {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /bunker " + usage());
            return;
        }

        Player player = (Player) sender;
        PlayerData playerData = BunkersCore.getInstance().getPlayerDataManager().getPlayerData(player);

        playerData.setPlayerBunker(BunkersCore.getInstance().getBunkerHandler().createBunker(playerData)); // Create random for now
    }

    @Override
    public String usage() {
        return "create";
    }
}
