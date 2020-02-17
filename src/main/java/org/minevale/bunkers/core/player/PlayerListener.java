package org.minevale.bunkers.core.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.minevale.bunkers.core.BunkersCore;

public class PlayerListener implements Listener {

    private final BunkersCore plugin;

    public PlayerListener(BunkersCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();


        plugin.getServer().getScheduler().runTask(plugin, () -> {
            PlayerData playerData = plugin.getPlayerDataManager().create(new PlayerData(player.getUniqueId(), player.getName()), false);
            if (playerData.getInventoryData() != null) {
                playerData.getInventoryData().apply(player);
            }
                playerData.setSyncing(false);
                playerData.getPlayerBunker().join(player);

        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);

        plugin.getPlayerDataManager().save(playerData);
    }


}
