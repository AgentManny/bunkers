package org.minevale.bunkers.core.util;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.world.World;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.bunker.BunkerHandler;
import org.minevale.bunkers.core.util.cuboid.Cuboid;

import java.io.File;

@UtilityClass
public final class WorldEditUtils {

    private static EditSession editSession;
    private static World worldEditWorld;

    public static void primeWorldEditApi() {
        if (editSession != null) {
            return;
        }

        EditSessionFactory esFactory = WorldEdit.getInstance().getEditSessionFactory();

        worldEditWorld = new BukkitWorld(BunkersCore.getInstance().getBunkerHandler().getWorld());
        editSession = esFactory.getEditSession(worldEditWorld, Integer.MAX_VALUE);
    }

    public static CuboidClipboard paste(File fileSchematic, Vector pasteAt) throws Exception {
        primeWorldEditApi();

        CuboidClipboard clipboard = SchematicFormat.MCEDIT.load(fileSchematic);
        clipboard.setOffset(new Vector(0, 0, 0));
        clipboard.paste(editSession, pasteAt, true);

        return clipboard;
    }

    public static void save(File schematicFile, Vector saveFrom) throws Exception {
        primeWorldEditApi();

        Vector schematicSize = readSchematicSize(schematicFile);

        CuboidClipboard newSchematic = new CuboidClipboard(schematicSize, saveFrom);
        newSchematic.copy(editSession);

        SchematicFormat.MCEDIT.save(newSchematic, schematicFile);
    }

    public static void clear(Cuboid bounds) {
        clear(
            new Vector(bounds.getLowerX(), bounds.getLowerY(), bounds.getLowerZ()),
            new Vector(bounds.getUpperX(), bounds.getUpperY(), bounds.getUpperZ())
        );
    }

    public static void clear(Vector lower, Vector upper) {
        primeWorldEditApi();

        BaseBlock air = new BaseBlock(Material.AIR.getId());
        Region region = new CuboidRegion(worldEditWorld, lower, upper);

        try {
            editSession.setBlocks(region, air);
        } catch (MaxChangedBlocksException ex) {
            // our block change limit is Integer.MAX_VALUE, so will never
            // have to worry about this happening
            throw new RuntimeException(ex);
        }
    }

    public static Vector readSchematicSize(File schematicFile) throws Exception {
        CuboidClipboard clipboard = SchematicFormat.MCEDIT.load(schematicFile);
        return clipboard.getSize();
    }

    public static Location vectorToLocation(Vector vector) {
        BunkerHandler bunkerHandler = BunkersCore.getInstance().getBunkerHandler();

        return new Location(
                bunkerHandler.getWorld(),
                vector.getBlockX(),
                vector.getBlockY(),
                vector.getBlockZ()
        );
    }

}