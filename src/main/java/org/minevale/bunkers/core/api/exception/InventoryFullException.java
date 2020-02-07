package org.minevale.bunkers.core.api.exception;

import org.bukkit.inventory.ItemStack;
import org.minevale.bunkers.core.player.PlayerData;

public class InventoryFullException extends PlayerEconomyException {

    public InventoryFullException(PlayerData player, ItemStack item) {
        super(player, "Cannot hold " + item.getType() + " x" + item.getAmount());
    }

    @Override
    public String getFriendlyMessage() {
        return player.getUsername() + " inventory is full, aborted adding";
    }
}
