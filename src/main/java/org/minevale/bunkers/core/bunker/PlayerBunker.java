package org.minevale.bunkers.core.bunker;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.player.inventory.PlayerInventoryData;
import org.minevale.bunkers.core.util.AngleUtil;
import org.minevale.bunkers.core.util.cuboid.Cuboid;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
public class PlayerBunker {

    private Cuboid bounds;
    private Location spawnLocation;

    // stored as (uuid -> player inventory)
    private Map<String, PlayerInventoryData> playerInventories = new HashMap<>(); // Stores in a map

    public PlayerBunker(Cuboid bounds) {
        this.bounds = bounds;
        scanSpawnLocation();
    }

    private void scanSpawnLocation() {
        for (Location location : bounds) {
            Block block = location.getBlock();
            Material type = block.getType();
            if (type == Material.SKULL_ITEM) {
                Skull skull = (Skull) block.getState();
                spawnLocation = location.clone().add(0.5, 1.5, 0.5);
                spawnLocation.setYaw(AngleUtil.faceToYaw(skull.getRotation()) + 90); // Fix location for YAW
                break; // Located spawn no need to continue
            }
        }
    }

    public static String serialize(PlayerBunker playerBunker) {
        return BunkersCore.GSON.toJson(playerBunker);
    }

    public static PlayerBunker deserialize(String json) {
        return BunkersCore.GSON.fromJson(json, PlayerBunker.class);
    }

    public static Document getAsDocument(PlayerBunker playerBunker) {
        return Document.parse(PlayerBunker.serialize(playerBunker));
    }

}
