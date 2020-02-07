package org.minevale.bunkers.core.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.player.PlayerData;

@RequiredArgsConstructor
public class PlayerSyncListener implements Listener {

    private static final String DENY_MESSAGE = ChatColor.RED + "Please wait, your account is still currently synchronising.";

    private final BunkersCore plugin;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData.isSyncing()) {
            event.setCancelled(true);
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);
            player.sendMessage(DENY_MESSAGE);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData.isSyncing()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData.isSyncing()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData.isSyncing()) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            player.sendMessage(DENY_MESSAGE);
        }
    }


}
