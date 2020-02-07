package org.minevale.bunkers.core.trade.player;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.minevale.bunkers.core.util.ItemBuilder;

@AllArgsConstructor
public enum PlayerTradeState {

    UNREADY(DyeColor.RED),
    READY(DyeColor.YELLOW),
    CONFIRM(DyeColor.LIME);

    private DyeColor color;

    public ItemStack getItem() {
        ItemBuilder builder = new ItemBuilder(Material.STAINED_GLASS_PANE);
        builder.data(color.getWoolData());
        switch (color) {
            case RED: {
                builder.name(ChatColor.RED + "Ready up the trade");
                break;
            }

            case YELLOW: {
                builder.name(ChatColor.YELLOW + "Click to confirm the trade");
                break;
            }

            case LIME: {
                builder.name(ChatColor.GREEN + "Trade confirmed");
                break;
            }
        }

        return builder.create();
    }

}
