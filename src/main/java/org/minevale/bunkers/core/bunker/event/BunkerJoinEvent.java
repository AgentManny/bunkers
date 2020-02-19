package org.minevale.bunkers.core.bunker.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.minevale.bunkers.core.player.bunker.PlayerBunker;

@Getter
public class BunkerJoinEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final PlayerBunker bunker;

    public BunkerJoinEvent(Player player, PlayerBunker bunker) {
        this.player = player;
        this.bunker = bunker;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
