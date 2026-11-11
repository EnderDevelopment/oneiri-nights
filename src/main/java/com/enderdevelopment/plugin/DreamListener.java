package com.enderdevelopment.plugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class DreamListener implements Listener {
    private final DreamManager dreamManager;

    public DreamListener(DreamManager dreamManager) {
        this.dreamManager = dreamManager;
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        dreamManager.handleBedEnter(event);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        dreamManager.restorePlayer(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        dreamManager.handleWorldChange(event.getPlayer());
    }
}
