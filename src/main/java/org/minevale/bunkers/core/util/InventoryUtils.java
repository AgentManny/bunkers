package org.minevale.bunkers.core.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {

    /**
     * Checks whether an item can be held inside an inventory
     *
     * @param item Item to add
     * @param target Inventory to check
     * @return
     */
    public static boolean fits(ItemStack item, Inventory target) {
        int leftToAdd = item.getAmount();
        if (target.getMaxStackSize() == Integer.MAX_VALUE) {
            return true;
        }
        for (ItemStack itemStack : target.getContents()) {
            if (leftToAdd <= 0) {
                return true;
            }
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                leftToAdd -= item.getMaxStackSize();
                continue;
            }
            if (!itemStack.isSimilar(item)) continue;
            leftToAdd -= itemStack.getMaxStackSize() - itemStack.getAmount();
        }
        return leftToAdd <= 0;
    }

}
