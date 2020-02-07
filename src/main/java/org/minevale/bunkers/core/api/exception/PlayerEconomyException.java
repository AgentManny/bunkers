package org.minevale.bunkers.core.api.exception;

import lombok.Getter;
import org.minevale.bunkers.core.player.PlayerData;

@Getter
public abstract class PlayerEconomyException extends Exception {

    protected final PlayerData player;
    private final String message;

    protected PlayerEconomyException(PlayerData player, String message) {
        super(player.getUsername() + "[" + player.getUuid().toString() + "]: " + message);
        this.message = message;
        this.player = player;
    }

    public String getFriendlyMessage() {
        return message;
    }
}
