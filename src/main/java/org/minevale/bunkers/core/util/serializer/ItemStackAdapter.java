package org.minevale.bunkers.core.util.serializer;

import com.google.gson.*;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import com.sun.org.apache.bcel.internal.generic.LOR;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Type;
import java.util.*;

@SuppressWarnings("deprecation")
public class ItemStackAdapter implements JsonDeserializer<ItemStack>, JsonSerializer<ItemStack> {

    public static final String ID = "id";
    public static final String COUNT = "count";

    public static final String NAME = "name";
    public static final String LORE = "lore";

    public static final String ENCHANTMENTS = "enchants";

    public static final String BOOK_TITLE = "title";
    public static final String BOOK_AUTHOR = "author";
    public static final String BOOK_PAGES = "pages";

    public static final String LEATHER_ARMOR_COLOR = "color";

    public static final String MAP_SCALING = "scaling";

    public static final String STORED_ENCHANTS = "stored-enchants";

    public static final String SKULL_OWNER = "skull";

    public static final String POTION_EFFECTS = "potion-effects";

    public JsonElement serialize(ItemStack item, Type type, JsonSerializationContext context) {
        return ItemStackAdapter.serialize(item);
    }

    public ItemStack deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        return ItemStackAdapter.deserialize(element);
    }

    public static JsonElement serialize(ItemStack item) {
        if (item == null) {
            item = new ItemStack(Material.AIR);
        }
        JsonObject element = new JsonObject();
        element.addProperty(ID, item.getTypeId());
        element.addProperty(ItemStackAdapter.getDataKey(item), item.getDurability());
        element.addProperty(COUNT, item.getAmount());
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                element.addProperty(NAME, meta.getDisplayName());
            }
            if (meta.hasLore()) {
                element.add(LORE, ItemStackAdapter.convertStringList(meta.getLore()));
            }
            if (meta instanceof LeatherArmorMeta) {
                element.addProperty(LEATHER_ARMOR_COLOR, ((LeatherArmorMeta) meta).getColor().asRGB());
            } else if (meta instanceof SkullMeta) {
                element.addProperty(SKULL_OWNER, ((SkullMeta) meta).getOwner());
            } else if (meta instanceof BookMeta) {
                element.addProperty(BOOK_TITLE, ((BookMeta) meta).getTitle());
                element.addProperty(BOOK_AUTHOR, ((BookMeta) meta).getAuthor());
                element.add(BOOK_PAGES, ItemStackAdapter.convertStringList(((BookMeta) meta).getPages()));
            } else if (meta instanceof PotionMeta) {
                if (!((PotionMeta) meta).getCustomEffects().isEmpty()) {
                    element.add(POTION_EFFECTS, ItemStackAdapter.convertPotionEffectList(((PotionMeta) meta).getCustomEffects()));
                }
            } else if (meta instanceof MapMeta) {
                element.addProperty(MAP_SCALING, ((MapMeta) meta).isScaling());
            } else if (meta instanceof EnchantmentStorageMeta) {
                JsonObject storedEnchantments = new JsonObject();
                for (Map.Entry entry : ((EnchantmentStorageMeta) meta).getStoredEnchants().entrySet()) {
                    storedEnchantments.addProperty(((Enchantment) entry.getKey()).getName(), (Number) entry.getValue());
                }
                element.add(STORED_ENCHANTS, storedEnchantments);
            }
        }
        if (item.getEnchantments().size() != 0) {
            JsonObject enchantments = new JsonObject();
            for (Map.Entry entry : item.getEnchantments().entrySet()) {
                enchantments.addProperty(((Enchantment) entry.getKey()).getName(), (Number) entry.getValue());
            }
            element.add(ENCHANTMENTS, enchantments);
        }
        return element;
    }

    public static ItemStack deserialize(JsonElement object) {
        JsonObject enchantments;
        if (!(object instanceof JsonObject)) {
            return new ItemStack(Material.AIR);
        }
        JsonObject element = (JsonObject) object;
        int id = element.get(ID).getAsInt();
        short data = element.has("damage") ? element.get("damage").getAsShort() : (element.has("data") ? element.get("data").getAsShort() : (short) 0);
        int count = element.get(COUNT).getAsInt();
        ItemStack item = new ItemStack(id, count, data);
        ItemMeta meta = item.getItemMeta();
        if (element.has(NAME)) {
            meta.setDisplayName(element.get(NAME).getAsString());
        }
        if (element.has(LORE)) {
            meta.setLore(ItemStackAdapter.convertStringList(element.get(LORE)));
        }
        if (element.has(LEATHER_ARMOR_COLOR)) {
            ((LeatherArmorMeta) meta).setColor(Color.fromRGB(element.get(LEATHER_ARMOR_COLOR).getAsInt()));
        } else if (element.has(SKULL_OWNER)) {
            ((SkullMeta) meta).setOwner(element.get(SKULL_OWNER).getAsString());
        } else if (element.has(BOOK_TITLE)) {
            ((BookMeta) meta).setTitle(element.get(BOOK_TITLE).getAsString());
            ((BookMeta) meta).setAuthor(element.get(BOOK_AUTHOR).getAsString());
            ((BookMeta) meta).setPages(ItemStackAdapter.convertStringList(element.get(BOOK_PAGES)));
        } else if (element.has(POTION_EFFECTS)) {
            PotionMeta potionMeta = (PotionMeta) meta;
            Iterator<PotionEffect> arrenchantment = ItemStackAdapter.convertPotionEffectList(element.get(POTION_EFFECTS)).iterator();
            while (arrenchantment.hasNext()) {
                PotionEffect effect = arrenchantment.next();
                potionMeta.addCustomEffect(effect, false);
            }
        } else if (element.has(MAP_SCALING)) {
            ((MapMeta) meta).setScaling(element.get(MAP_SCALING).getAsBoolean());
        } else if (element.has(STORED_ENCHANTS)) {
            enchantments = (JsonObject) element.get(STORED_ENCHANTS);
            for (Enchantment enchantment : Enchantment.values()) {
                if (!enchantments.has(enchantment.getName())) continue;
                ((EnchantmentStorageMeta) meta).addStoredEnchant(enchantment, enchantments.get(enchantment.getName()).getAsInt(), true);
            }
        }
        item.setItemMeta(meta);
        if (element.has(ENCHANTMENTS)) {
            enchantments = (JsonObject) element.get(ENCHANTMENTS);
            for (Enchantment enchantment : Enchantment.values()) {
                if (!enchantments.has(enchantment.getName())) continue;
                item.addUnsafeEnchantment(enchantment, enchantments.get(enchantment.getName()).getAsInt());
            }
        }
        return item;
    }

    private static String getDataKey(ItemStack item) {
        if (item.getType() == Material.AIR) {
            return "data";
        }
        if (Enchantment.DURABILITY.canEnchantItem(item)) {
            return "damage";
        }
        return "data";
    }

    public static JsonArray convertStringList(Collection<String> strings) {
        JsonArray ret = new JsonArray();
        for (String string : strings) {
            ret.add(new JsonPrimitive(string));
        }
        return ret;
    }

    public static List<String> convertStringList(JsonElement jsonElement) {
        JsonArray array = jsonElement.getAsJsonArray();
        ArrayList<String> ret = new ArrayList<>();
        for (JsonElement element : array) {
            ret.add(element.getAsString());
        }
        return ret;
    }

    public static JsonArray convertPotionEffectList(Collection<PotionEffect> potionEffects) {
        JsonArray ret = new JsonArray();
        for (PotionEffect e : potionEffects) {
            ret.add(PotionEffectAdapter.toJson(e));
        }
        return ret;
    }

    public static List<PotionEffect> convertPotionEffectList(JsonElement jsonElement) {
        if (jsonElement == null) {
            return null;
        }
        if (!jsonElement.isJsonArray()) {
            return null;
        }
        JsonArray array = jsonElement.getAsJsonArray();
        ArrayList<PotionEffect> ret = new ArrayList<>();
        for (JsonElement element : array) {
            PotionEffect e = PotionEffectAdapter.fromJson(element);
            if (e == null) continue;
            ret.add(e);
        }
        return ret;
    }

}

