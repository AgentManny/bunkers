package org.minevale.bunkers.core.player.balance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.minevale.bunkers.core.player.inventory.PlayerInventoryData;

@Getter
@AllArgsConstructor
public enum Balance {

    COINS("coins", Material.DOUBLE_PLANT),
    NUGGETS("nuggets", Material.GOLD_NUGGET),
    BARS("bars", Material.GOLD_INGOT);

    private final String id;
    private final Material type;

    public int getAmountFromInventory(PlayerInventoryData inventoryData) {
        int amount = 0;
        for (ItemStack item : inventoryData.getContents()) {
            if (item.getType() == type) {
                amount += item.getAmount();
            }
        }
        return amount;
    }

}
