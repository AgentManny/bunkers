package org.minevale.bunkers.core.bunker;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.plugin.java.JavaPlugin;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.player.PlayerData;
import org.minevale.bunkers.core.player.bunker.PlayerBunker;
import org.minevale.bunkers.core.util.WorldEditUtils;
import org.minevale.bunkers.core.util.cuboid.Cuboid;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Getter
public class BunkerHandler {

    public static final File SCHEMATICS_FOLDER = new File(JavaPlugin.getPlugin(WorldEditPlugin.class).getDataFolder(), "schematics");
    public static final Vector STARTING_POINT = new Vector(1000, 80, 1000);

    public final int gridSpacing;

    private final BunkersCore plugin;

    private final File schematic;
    private final World world;

    private int copies = 1; // How many bunkers created instead of storing each bunker in a weak map
    private boolean requireSave = false;

    public BunkerHandler(BunkersCore plugin) {
        this.plugin = plugin;

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
                    .generatorSettings("0;0;0;0") // Simple way to make it void xd
                    .generateStructures(false)
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

        Document document = plugin.getPlayerDataManager().getMongoCollection().find(Filters.eq("_id", "bunkers")).first();
        if (document != null) {
            this.copies = document.getInteger("copies", 1);
            save(true);
        }

        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> save(false), TimeUnit.MINUTES.toMillis(10), TimeUnit.MINUTES.toMillis(10));
    }

    public void save(boolean force) {
        if (force || requireSave) {
            System.out.println("Saving " + copies + " bunkers");
            plugin.getPlayerDataManager().getMongoCollection().replaceOne(Filters.eq("_id", "bunkers"), new Document("_id", "bunkers").append("copies", copies), new ReplaceOptions().upsert(true));
            requireSave = false;
        }
    }

    public PlayerBunker createBunker(PlayerData playerData) {
        copies += 1;
        requireSave = true;


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

        return new PlayerBunker(playerData, new Cuboid(lowerCorner, upperCorner));
    }

}
