package org.minevale.bunkers.core.trade.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class PlayerTradeRequest {

    private UUID sender;
    private UUID target;

    private long alive;

    public boolean isValid() {
        return alive > System.currentTimeMillis();
    }
}
