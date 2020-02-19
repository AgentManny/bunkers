package org.minevale.bunkers.core.player.bunker;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.bunker.event.BunkerJoinEvent;
import org.minevale.bunkers.core.player.PlayerData;
import org.minevale.bunkers.core.util.AngleUtil;
import org.minevale.bunkers.core.util.cuboid.Cuboid;

@Getter
@NoArgsConstructor
public class PlayerBunker {

    @Setter private transient PlayerData playerData;

    private Cuboid bounds;
    private Location spawnLocation;

    public PlayerBunker(PlayerData playerData, Cuboid bounds) {
        this.playerData = playerData;
        this.bounds = bounds;
        scanSpawnLocation();
    }

    public void join(Player player) {
        player.sendMessage(ChatColor.GREEN + "Teleported to " + playerData.getUsername() + "'s bunker");
        player.teleport(getSpawnLocation());
        BunkersCore.getInstance().getServer().getPluginManager().callEvent(new BunkerJoinEvent(player, this));
    }

    public Location getSpawnLocation() {
        Preconditions.checkNotNull(this.spawnLocation);
        return spawnLocation;
    }

    private void scanSpawnLocation() {
        for (Location location : bounds) {
            Block block = location.getBlock();
            Material type = block.getType();
            if (type == Material.SKULL ||type == Material.SKULL_ITEM) {
                Skull skull = (Skull) block.getState();
                spawnLocation = location.clone().add(0.5, 1.5, 0.5);
                spawnLocation.setYaw(AngleUtil.faceToYaw(skull.getRotation()) + 90); // Fix location for YAW
                location.getBlock().setType(Material.AIR);
                break; // Located spawn no need to continue
            }
        }

        if (this.spawnLocation == null) {
            throw new RuntimeException("Spawn for bunkers isn't set -- You must place a skull");
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
