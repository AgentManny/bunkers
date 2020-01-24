package org.minevale.bunkers.core.trade;

import lombok.Getter;
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

}
