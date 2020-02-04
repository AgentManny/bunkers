package org.minevale.bunkers.core.api.exception;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryFullException extends PlayerEconomyException {

    public InventoryFullException(Player player, Inventory inventory, ItemStack item) {
        super(player, inventory.getName() + " cannot hold " + item.getType() + " x" + item.getAmount());
    }

    @Override
    public String getFriendlyMessage() {
        return player.getName() + " inventory is full, aborted adding";
    }
}
