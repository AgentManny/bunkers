package org.minevale.bunkers.core.trade;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.trade.player.PlayerTradeRequest;

import java.util.HashSet;
import java.util.Set;

public class TradeManager {

    private final BunkersCore plugin;

    @Getter
    private Set<PlayerTradeRequest> playerTradeRequests = new HashSet<>();

    public TradeManager(BunkersCore plugin) {
        this.plugin = plugin;
    }

    /**
     * Checks if a player has a pending trade request
     *
     * @param player Player to check
     * @return Whether they have a pending target request
     */
    public boolean hasTradeRequest(Player player) {
        return playerTradeRequests.stream().anyMatch(request -> request.getSender() == player.getUniqueId() || request.getTarget() == player.getUniqueId());
    }

}
