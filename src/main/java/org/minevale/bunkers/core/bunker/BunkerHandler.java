package org.minevale.bunkers.core.bunker;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.plugin.java.JavaPlugin;
import org.minevale.bunkers.core.BunkersCore;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Getter
public class BunkerHandler {

    public static final File SCHEMATICS_FOLDER = new File(JavaPlugin.getPlugin(WorldEditPlugin.class).getDataFolder(), "schematics");

    private final BunkersCore plugin;

    private final File schematic;
    private final World world;

    private final BunkerGrid grid;

    private Map<Integer, PlayerBunker> createdBunkers = new HashMap<>(); // Amount of bunkers created -- todo maybe change just store copies created in YAML (or to support multi server in future MONGO)

    public BunkerHandler(BunkersCore plugin) {
        this.plugin = plugin;
        this.grid = new BunkerGrid();

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

        String schematicName = plugin.getConfig().getString("bunker.world.name", "example");
        this.schematic = new File(SCHEMATICS_FOLDER, schematicName + ".schematic");
        if (!schematic.exists()) {
            plugin.getLogger().warning("Bunker " + schematicName + " schematic not found.");
            // plugin.getPluginLoader().disablePlugin(plugin); -- Commented out for debug
            return;
        }
    }


}
