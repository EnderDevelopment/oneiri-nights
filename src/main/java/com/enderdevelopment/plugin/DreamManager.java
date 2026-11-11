package com.enderdevelopment.plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.PolarBear;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class DreamManager {
    public static final String WORLD_NAME = "dream_world";
    private static final int ENTITY_COUNT = 3;
    private static final double WAKE_DISTANCE = 2.75D;

    private final JavaPlugin plugin;
    private final World dreamWorld;
    private final NamespacedKey dreamEntityKey;
    private final Map<UUID, Location> returnLocations = new HashMap<>();
    private final Map<UUID, Set<UUID>> playerEntities = new HashMap<>();
    private BukkitTask chaseMonitor;

    public DreamManager(JavaPlugin plugin, World dreamWorld) {
        this.plugin = plugin;
        this.dreamWorld = dreamWorld;
        this.dreamEntityKey = new NamespacedKey(plugin, "dream_entity");
    }

    public void handleBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }
        Player player = event.getPlayer();
        if (returnLocations.containsKey(player.getUniqueId())) {
            return;
        }

        event.setCancelled(true);
        Location original = player.getLocation().clone();
        Bukkit.getScheduler().runTask(plugin, () -> enterDream(player, original));
    }

    private void enterDream(Player player, Location original) {
        if (!player.isOnline() || returnLocations.containsKey(player.getUniqueId())) {
            return;
        }
        returnLocations.put(player.getUniqueId(), original);
        Location dreamLocation = new Location(dreamWorld, 0.5D, 80.0D, 0.5D);
        player.teleport(dreamLocation);
        player.sendMessage("You drift into a white dream...");

        Set<UUID> entities = new HashSet<>();
        for (int index = 0; index < ENTITY_COUNT; index++) {
            double angle = (Math.PI * 2.0D * index) / ENTITY_COUNT;
            Location spawn = dreamLocation.clone().add(Math.cos(angle) * 10.0D, 0.0D, Math.sin(angle) * 10.0D);
            PolarBear bear = (PolarBear) dreamWorld.spawnEntity(spawn, EntityType.POLAR_BEAR);
            bear.getPersistentDataContainer().set(dreamEntityKey, PersistentDataType.BYTE, (byte) 1);
            bear.setTarget(player);
            entities.add(bear.getUniqueId());
        }
        playerEntities.put(player.getUniqueId(), entities);
    }

    public void startChaseMonitor() {
        chaseMonitor = Bukkit.getScheduler().runTaskTimer(plugin, this::checkChases, 1L, 5L);
    }

    private void checkChases() {
        for (UUID playerId : Set.copyOf(returnLocations.keySet())) {
            Player player = Bukkit.getPlayer(playerId);
            if (player == null || !player.isOnline()) {
                continue;
            }
            Set<UUID> entities = playerEntities.getOrDefault(playerId, Set.of());
            for (UUID entityId : entities) {
                Entity entity = Bukkit.getEntity(entityId);
                if (entity == null || !entity.isValid()) {
                    continue;
                }
                if (entity.getLocation().distanceSquared(player.getLocation()) <= WAKE_DISTANCE * WAKE_DISTANCE) {
                    restorePlayer(player);
                    break;
                }
            }
        }
    }

    public void handleWorldChange(Player player) {
        if (!dreamWorld.equals(player.getWorld()) && returnLocations.containsKey(player.getUniqueId())) {
            restorePlayer(player);
        }
    }

    public void restorePlayer(Player player) {
        UUID playerId = player.getUniqueId();
        Location returnLocation = returnLocations.remove(playerId);
        removeDreamEntities(playerId);
        if (returnLocation != null && player.isOnline()) {
            player.teleport(returnLocation);
            player.sendMessage("You awaken from the dream.");
        }
    }

    public void restoreAllPlayers() {
        if (chaseMonitor != null) {
            chaseMonitor.cancel();
        }
        for (UUID playerId : Set.copyOf(returnLocations.keySet())) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                restorePlayer(player);
            } else {
                removeDreamEntities(playerId);
                returnLocations.remove(playerId);
            }
        }
    }

    private void removeDreamEntities(UUID playerId) {
        Set<UUID> entities = playerEntities.remove(playerId);
        if (entities == null) {
            return;
        }
        for (UUID entityId : entities) {
            Entity entity = Bukkit.getEntity(entityId);
            if (entity != null) {
                entity.remove();
            }
        }
    }
}
