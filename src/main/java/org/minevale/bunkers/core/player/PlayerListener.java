package org.minevale.bunkers.core.player;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.minevale.bunkers.core.BunkersCore;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final BunkersCore plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerDataManager().create(new PlayerData(player.getUniqueId(), player.getName()), false);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);


        plugin.getPlayerDataManager().save(playerData);
    }


}