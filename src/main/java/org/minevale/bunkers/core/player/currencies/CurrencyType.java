package org.minevale.bunkers.core.player.currencies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.minevale.bunkers.core.player.inventory.PlayerInventoryData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum CurrencyType {

    COINS("coins", Material.DOUBLE_PLANT, 1, null),
    NUGGETS("nuggets", Material.GOLD_NUGGET, 64, null),
    BARS("bars", Material.GOLD_INGOT, 4096, null);

    private final String id;
    private final Material type;

    private int value;

    @Setter private ItemStack item;

    public static void init(FileConfiguration config) {
        Set<String> section = config.getConfigurationSection("currency.items").getKeys(false);
        for (String source : section) {
            CurrencyType balance = parse(source);
            if (balance == null) {
                System.out.println("Failed to load " + source + " item as the currency assigned for it doesn't exist");
                continue;
            }

            ItemStack item = new ItemStack(balance.getType(), 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("currency.items." + source + ".name", "Unknown")));

            List<String> lore = new ArrayList<>();
            if (config.contains("currency.items." + source + ".lore")) {
                config.getStringList("currency.items." + source + ".lore").forEach(line ->
                        lore.add(ChatColor.translateAlternateColorCodes('&', line))
                );
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            balance.setItem(item);
        }
    }


    public int getAmountFromInventory(PlayerInventoryData inventoryData) {
        int amount = 0;
        for (ItemStack item : inventoryData.getContents()) {
            if (item != null && item.getType() == type) {
                amount += item.getAmount();
            }
        }
        return amount;
    }

    public String getFriendlyName() {
        return WordUtils.capitalizeFully(id);
    }

    public static CurrencyType parse(String source) {
        for (CurrencyType balance : CurrencyType.values()) {
            if (balance.getId().equalsIgnoreCase(source) || balance.name().equalsIgnoreCase(source)) {
                return balance;
            }
        }
        return null;
    }
}
