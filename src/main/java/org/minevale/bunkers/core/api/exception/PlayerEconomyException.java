package org.minevale.bunkers.core.api.exception;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public abstract class PlayerEconomyException extends Exception {

    protected final Player player;
    private final String message;

    protected PlayerEconomyException(Player player, String message) {
        super(player.getName() + "[" + player.getUniqueId().toString() + "]: " + message);
        this.message = message;
        this.player = player;
    }

    public String getFriendlyMessage() {
        return message;
    }
}
