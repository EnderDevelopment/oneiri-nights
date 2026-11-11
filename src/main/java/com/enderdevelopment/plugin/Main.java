package com.enderdevelopment.plugin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private DreamManager dreamManager;

    @Override
    public void onEnable() {
        World dreamWorld = Bukkit.getWorld(DreamManager.WORLD_NAME);
        if (dreamWorld == null) {
            WorldCreator creator = new WorldCreator(DreamManager.WORLD_NAME);
            creator.environment(World.Environment.NORMAL);
            creator.generateStructures(false);
            dreamWorld = Bukkit.createWorld(creator);
        }
        if (dreamWorld == null) {
            getLogger().severe("The dream world could not be created.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        dreamManager = new DreamManager(this, dreamWorld);
        Bukkit.getPluginManager().registerEvents(new DreamListener(dreamManager), this);
        dreamManager.startChaseMonitor();
    }

    @Override
    public void onDisable() {
        if (dreamManager != null) {
            dreamManager.restoreAllPlayers();
        }
    }
}
