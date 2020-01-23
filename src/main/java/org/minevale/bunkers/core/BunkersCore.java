package org.minevale.bunkers.core;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class BunkersCore extends JavaPlugin {

    @Getter
    private static BunkersCore instance;

    public void onEnable() {
        instance = this;
    }
}
