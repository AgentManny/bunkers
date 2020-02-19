package org.minevale.bunkers.core.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.bunker.event.BunkerJoinEvent;
import org.minevale.bunkers.core.util.Border;

@RequiredArgsConstructor
public class BorderListener implements Listener {

    private final BunkersCore plugin;

    @EventHandler
    public void onBunkerJoin(BunkerJoinEvent event) {
        Border.handleBorderUpdate(event.getPlayer(), event.getBunker());
    }

}
