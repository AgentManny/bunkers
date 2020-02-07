package org.minevale.bunkers.core.player.inventory;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Validate;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.player.PlayerData;
import org.minevale.bunkers.core.util.InventoryUtils;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class PlayerInventoryData {

    private final PlayerData playerData;

    private PotionEffect[] effects;

    private int health;
    private int hunger;

    private ItemStack[] armor;
    private ItemStack[] contents;

    public PlayerInventoryData(PlayerData player) {
        this.playerData = player;

        update(player);
    }

    public void update(PlayerData playerData) {
        if (playerData.getPlayer() != null) {
            update(playerData.getPlayer());
        }
    }

    public void update(Player player) {
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

    public void removeAmount(Material material, int amount) {
        int removed = 0;
        for (int i = 0; i < this.contents.length; i++) {
            if (removed >= amount) {
                return;
            }

            ItemStack itemStack = this.getItem(i);
            if (itemStack == null) {
                continue;
            }

            if (itemStack.getType() == material) {
                removed += itemStack.getAmount();
                int newAmount = itemStack.getAmount() - amount;
                if (newAmount <= 0) {
                    this.setItem(i, null);
                } else {
                    itemStack.setAmount(newAmount);
                }
            }
        }

        Player bukkitPlayer = playerData.getPlayer();
        if (bukkitPlayer != null) {
            bukkitPlayer.getInventory().removeAmount(material, amount);
        }
    }

    public boolean contains(int materialId) {
        for (ItemStack item : getContents()) {
            if (item != null && item.getTypeId() == materialId) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Material material) {
        Validate.notNull(material, "Material cannot be null");
        return contains(material.getId());
    }

    public boolean contains(ItemStack item) {
        if (item == null) {
            return false;
        }
        for (ItemStack i : getContents()) {
            if (item.equals(i)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(int materialId, int amount) {
        if (amount <= 0) {
            return true;
        }
        for (ItemStack item : getContents()) {
            if (item != null && item.getTypeId() == materialId) {
                if ((amount -= item.getAmount()) <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean contains(Material material, int amount) {
        Validate.notNull(material, "Material cannot be null");
        return contains(material.getId(), amount);
    }

    public boolean contains(ItemStack item, int amount) {
        if (item == null) {
            return false;
        }
        if (amount <= 0) {
            return true;
        }
        for (ItemStack i : getContents()) {
            if (item.equals(i) && --amount <= 0) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAtLeast(ItemStack item, int amount) {
        if (item == null) {
            return false;
        }
        if (amount <= 0) {
            return true;
        }
        for (ItemStack i : getContents()) {
            if (item.isSimilar(i) && (amount -= i.getAmount()) <= 0) {
                return true;
            }
        }
        return false;
    }

    public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
        Validate.noNullElements(items, "Item cannot be null");
        HashMap<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

        /* TODO: some optimization
         *  - Create a 'firstPartial' with a 'fromIndex'
         *  - Record the lastPartial per Material
         *  - Cache firstEmpty result
         */

        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            while (true) {
                // Do we already have a stack of it?
                int firstPartial = firstPartial(item);

                // Drat! no partial stack
                if (firstPartial == -1) {
                    // Find a free spot!
                    int firstFree = firstEmpty();

                    if (firstFree == -1) {
                        // No space at all!
                        leftover.put(i, item);
                        break;
                    } else {
                        // More than a single stack!
                        if (item.getAmount() > InventoryUtils.MAX_ITEM_STACK) {
                            CraftItemStack stack = CraftItemStack.asCraftCopy(item);
                            stack.setAmount(InventoryUtils.MAX_ITEM_STACK);
                            setItem(firstFree, stack);
                            item.setAmount(item.getAmount() - InventoryUtils.MAX_ITEM_STACK);
                        } else {
                            // Just store it
                            setItem(firstFree, item);
                            break;
                        }
                    }
                } else {
                    // So, apparently it might only partially fit, well lets do just that
                    ItemStack partialItem = getItem(firstPartial);

                    int amount = item.getAmount();
                    int partialAmount = partialItem.getAmount();
                    int maxAmount = partialItem.getMaxStackSize();

                    // Check if it fully fits
                    if (amount + partialAmount <= maxAmount) {
                        partialItem.setAmount(amount + partialAmount);
                        // To make sure the packet is sent to the client
                        setItem(firstPartial, partialItem);
                        break;
                    }

                    // It fits partially
                    partialItem.setAmount(maxAmount);
                    // To make sure the packet is sent to the client
                    setItem(firstPartial, partialItem);
                    item.setAmount(amount + partialAmount - maxAmount);
                }
            }
        }

        Player bukkitPlayer = playerData.getPlayer();
        if (bukkitPlayer != null) {
            bukkitPlayer.getInventory().addItem(items);
        }
        return leftover;
    }


    public Map<Integer, ItemStack> all(int materialId) {
        Map<Integer, ItemStack> slots = new HashMap<>();

        ItemStack[] inventory = getContents();
        for (int i = 0; i < inventory.length; i++) {
            ItemStack item = inventory[i];
            if (item != null && item.getTypeId() == materialId) {
                slots.put(i, item);
            }
        }

        Player bukkitPlayer = playerData.getPlayer();
        if (bukkitPlayer != null) {
            bukkitPlayer.getInventory().all(materialId);
        }
        return slots;
    }


    public HashMap<Integer, ItemStack> all(ItemStack item) {
        HashMap<Integer, ItemStack> slots = new HashMap<>();
        if (item != null) {
            ItemStack[] inventory = getContents();
            for (int i = 0; i < inventory.length; i++) {
                if (item.equals(inventory[i])) {
                    slots.put(i, inventory[i]);
                }
            }
        }

        Player bukkitPlayer = playerData.getPlayer();
        if (bukkitPlayer != null) {
            bukkitPlayer.getInventory().all(item);
        }
        return slots;
    }

    public int first(int materialId) {
        ItemStack[] inventory = getContents();
        for (int i = 0; i < inventory.length; i++) {
            ItemStack item = inventory[i];
            if (item != null && item.getTypeId() == materialId) {
                return i;
            }
        }
        Player bukkitPlayer = playerData.getPlayer();
        if (bukkitPlayer != null) {
            bukkitPlayer.getInventory().first(materialId);
        }
        return -1;
    }

    private int first(ItemStack item, boolean withAmount) {
        if (item == null) {
            return -1;
        }
        ItemStack[] inventory = getContents();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) continue;

            if (withAmount ? item.equals(inventory[i]) : item.isSimilar(inventory[i])) {
                return i;
            }
        }


        return -1;
    }

    public int firstEmpty() {
        ItemStack[] inventory = getContents();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public int firstPartial(int materialId) {
        ItemStack[] inventory = getContents();
        for (int i = 0; i < inventory.length; i++) {
            ItemStack item = inventory[i];
            if (item != null && item.getTypeId() == materialId && item.getAmount() < item.getMaxStackSize()) {
                return i;
            }
        }
        return -1;
    }

    public int firstPartial(Material material) {
        Validate.notNull(material, "Material cannot be null");
        return firstPartial(material.getId());
    }

    private int firstPartial(ItemStack item) {
        ItemStack[] inventory = getContents();
        ItemStack filteredItem = CraftItemStack.asCraftCopy(item);
        if (item == null) {
            return -1;
        }
        for (int i = 0; i < inventory.length; i++) {
            ItemStack cItem = inventory[i];
            if (cItem != null && cItem.getAmount() < cItem.getMaxStackSize() && cItem.isSimilar(filteredItem)) {
                return i;
            }
        }
        return -1;
    }

    public ItemStack getItem(int i) {
        ItemStack[] aitemstack = this.contents;

        if (i >= aitemstack.length) {
            i -= aitemstack.length;
            aitemstack = this.armor;
        }

        return aitemstack[i];
    }

    public void setItem(int index, ItemStack item) {
        contents[index] = ((item == null || item.getTypeId() == 0) ? null : item);
        Player bukkitPlayer = playerData.getPlayer();
        if (bukkitPlayer != null) {
            bukkitPlayer.getInventory().setItem(index, item);
        }
    }

    public HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
        Validate.notNull(items, "Items cannot be null");
        HashMap<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

        // TODO: optimization

        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            int toDelete = item.getAmount();

            while (true) {
                int first = first(item, false);

                // Drat! we don't have this type in the inventory
                if (first == -1) {
                    item.setAmount(toDelete);
                    leftover.put(i, item);
                    break;
                } else {
                    ItemStack itemStack = getItem(first);
                    int amount = itemStack.getAmount();

                    if (amount <= toDelete) {
                        toDelete -= amount;
                        // clear the slot, all used up
                        clear(first);
                    } else {
                        // split the stack and store
                        itemStack.setAmount(amount - toDelete);
                        setItem(first, itemStack);
                        toDelete = 0;
                    }
                }

                // Bail when done
                if (toDelete <= 0) {
                    break;
                }
            }
        }
        return leftover;
    }

    public void remove(int materialId) {
        ItemStack[] items = getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getTypeId() == materialId) {
                clear(i);
            }
        }

    }

    public void remove(Material material) {
        Validate.notNull(material, "Material cannot be null");
        remove(material.getId());
    }

    public void remove(ItemStack item) {
        ItemStack[] items = getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].equals(item)) {
                clear(i);
            }
        }
    }

    public void clear(int index) {
        setItem(index, null);
    }

    public static String serialize(PlayerInventoryData inventoryData) {
        return BunkersCore.GSON.toJson(inventoryData);
    }

    public static PlayerInventoryData deserialize(String json) {
        return BunkersCore.GSON.fromJson(json, PlayerInventoryData.class);
    }

    public static Document getAsDocument(PlayerInventoryData inventoryData) {
        return Document.parse(PlayerInventoryData.serialize(inventoryData));
    }


}
