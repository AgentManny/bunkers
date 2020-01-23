package org.minevale.bunkers.core.player.inventory;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.minevale.bunkers.core.BunkersCore;

@Getter
@Setter
public class PlayerInventoryData {

    private final PotionEffect[] effects;

    private final int health;
    private final int hunger;

    private final ItemStack[] armor;
    private final ItemStack[] contents;

    public PlayerInventoryData(Player player) {
        this.contents = player.getInventory().getContents();
        for (int i = 0; i < this.contents.length; ++i) {
            ItemStack stack = this.contents[i];
            if (stack != null) continue;
            this.contents[i] = new ItemStack(Material.AIR, 0, (short) 0);
        }
        this.armor = player.getInventory().getArmorContents();
        for (int i = 0; i < this.armor.length; ++i) {
            ItemStack stack = this.armor[i];
            if (stack != null) continue;
            this.armor[i] = new ItemStack(Material.AIR, 0, (short) 0);
        }
        this.effects = player.getActivePotionEffects().toArray(new PotionEffect[0]);
        this.health = (int) player.getHealth();
        this.hunger = player.getFoodLevel();
    }

    public void apply(Player player) {
        player.getInventory().setContents(this.contents);
        player.getInventory().setArmorContents(this.armor);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        for (PotionEffect effect : this.effects) {
            player.addPotionEffect(effect);
        }
    }

    public static String serialize(Player player) {
        return BunkersCore.GSON.toJson(new PlayerInventoryData(player));
    }

    public static PlayerInventoryData deserialize(String json) {
        return BunkersCore.GSON.fromJson(json, PlayerInventoryData.class);
    }

    public static Document getAsDocument(Player player) {
        return Document.parse(PlayerInventoryData.serialize(player));
    }


}
