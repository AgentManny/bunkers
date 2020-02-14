package org.minevale.bunkers.core.bunker;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.player.PlayerData;
import org.minevale.bunkers.core.player.bunker.PlayerBunker;
import org.minevale.bunkers.core.util.WorldEditUtils;
import org.minevale.bunkers.core.util.cuboid.Cuboid;

import java.io.File;

@Getter
public class BunkerHandler {

    public static final File SCHEMATICS_FOLDER = new File(JavaPlugin.getPlugin(WorldEditPlugin.class).getDataFolder(), "schematics");
    public static final Vector STARTING_POINT = new Vector(1000, 80, 1000);

    public final int gridSpacing;

    private final BunkersCore plugin;

    private final File schematic;
    private final World world;

    private int copies; // How many bunkers created instead of storing each bunker in a weak map

    public BunkerHandler(BunkersCore plugin) {
        this.plugin = plugin;

        this.copies = plugin.getConfig().getInt("bunker.copes", 1);
        this.gridSpacing = plugin.getConfig().getInt("bunker.grid-spacing", 150);

        String worldName = plugin.getConfig().getString("bunker.world.name", "bunkers");
        World world;
        if((world = plugin.getServer().getWorld(worldName)) != null) {
            plugin.getLogger().warning(worldName + " is already loaded, could cause conflicts.");
        } else {
            String environmentName = plugin.getConfig().getString("bunker.world.environment", "NORMAL");
            World.Environment environment = World.Environment.valueOf(environmentName.toUpperCase()) == null ? World.Environment.NORMAL : World.Environment.valueOf(environmentName.toUpperCase());
            world = new WorldCreator(worldName)
                    .environment(environment)
                    .type(WorldType.FLAT)
                    .createWorld();
        }
        this.world = world;

        String schematicName = plugin.getConfig().getString("bunker.schematic", "example");
        this.schematic = new File(SCHEMATICS_FOLDER, schematicName + ".schematic");
        if (!schematic.exists()) {
            plugin.getLogger().warning("Bunker " + schematicName + " schematic not found.");
            // plugin.getPluginLoader().disablePlugin(plugin); -- Commented out for debug
            return;
        }
    }

    public PlayerBunker createBunker(PlayerData playerData) {
        plugin.getConfig().set("bunker.copies", copies += 1);

        int xStart = STARTING_POINT.getBlockX() + gridSpacing; // todo introduce a proper grid it'll continue incrementing Z loc
        int zStart = STARTING_POINT.getBlockZ() + (gridSpacing * copies);

        Vector pasteAt = new Vector(xStart, STARTING_POINT.getY(), zStart);

        CuboidClipboard clipboard;
        try {
            clipboard = WorldEditUtils.paste(schematic, pasteAt);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        Location lowerCorner = WorldEditUtils.vectorToLocation(pasteAt);
        Location upperCorner = WorldEditUtils.vectorToLocation(pasteAt.add(clipboard.getSize()));

        PlayerBunker bunker = new PlayerBunker(playerData, new Cuboid(lowerCorner, upperCorner));

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            Player bukkitPlayer = playerData.getPlayer();
            if (bukkitPlayer != null) {
                bukkitPlayer.sendMessage(ChatColor.GREEN + "Bunker created. Teleporting...");
                bukkitPlayer.teleport(bunker.getBounds().getCenter());
            }
        }, 20L);

        return bunker;
    }

}
