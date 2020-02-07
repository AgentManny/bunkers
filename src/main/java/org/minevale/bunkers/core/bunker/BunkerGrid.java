package org.minevale.bunkers.core.bunker;

import com.sk89q.worldedit.Vector;
import lombok.Getter;
import lombok.Setter;
import org.minevale.bunkers.core.BunkersCore;

public class BunkerGrid {

    public static final Vector STARTING_POINT = new Vector(1_000, 80, 1_000);

    public static final int GRID_SPACING_X = 150;
    public static final int GRID_SPACING_Z = 150;

    @Getter @Setter private int copies; // How many bunkers created instead of storing each bunker in a weak map

    public BunkerGrid() {
        copies = BunkersCore.getInstance().getConfig().getInt("bunker.copies", 1);
    }


}
