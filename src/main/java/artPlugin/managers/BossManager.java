// managers/BossManager.java
package artPlugin.managers;

import artPlugin.Main;
import artPlugin.models.Boss;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Entity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossManager {
    private final Main plugin;
    private final Map<String, Boss> bosses = new HashMap<>();
    private final Map<UUID, String> spawnedBosses = new HashMap<>();

    public BossManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadBosses() {
        bosses.clear();
        File folder = new File(plugin.getDataFolder(), "bosses");

        if (!folder.exists()) {
            folder.mkdirs();
            return;
        }

        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (!file.getName().endsWith(".yml")) continue;

            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                String id = file.getName().replace(".yml", "");

                // Parse spawn location
                String worldName = config.getString("spawn.world", "world");
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    plugin.getLogger().warning("World not found for boss " + id + ": " + worldName);
                    continue;
                }

                Location spawnLoc = new Location(world,
                        config.getDouble("spawn.x", 0),
                        config.getDouble("spawn.y", 64),
                        config.getDouble("spawn.z", 0)
                );

                Boss boss = new Boss(
                        id,
                        config.getString("name", "Unknown Boss"),
                        config.getString("entity_type", "ZOMBIE"),
                        config.getDouble("health", 100),
                        config.getDouble("damage", 10),
                        config.getDouble("speed", 1.0),
                        config.getConfigurationSection("effects"),
                        spawnLoc,
                        config.getInt("spawn.radius", 0),
                        config.getStringList("drop_artifacts")
                );

                bosses.put(id, boss);
                plugin.getLogger().info("Loaded boss: " + boss.getName());
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load boss from " + file.getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        plugin.getLogger().info("Loaded " + bosses.size() + " bosses total.");
    }

    public Entity spawnBoss(String bossId) {
        Boss boss = bosses.get(bossId);
        if (boss == null) {
            plugin.getLogger().warning("Boss not found: " + bossId);
            return null;
        }

        try {
            Entity entity = boss.getSpawnLocation().getWorld().spawnEntity(boss.getSpawnLocation(), boss.getEntityType());

            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                livingEntity.setCustomName("§c" + boss.getName());
                livingEntity.setCustomNameVisible(true);

                // Set health - using correct attribute names for Paper 1.21+
                if (livingEntity.getAttribute(Attribute.MAX_HEALTH) != null) {
                    livingEntity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(boss.getHealth());
                    livingEntity.setHealth(boss.getHealth());
                    plugin.getLogger().info("Set health for " + boss.getName() + " to " + boss.getHealth());
                }

                // Set movement speed - using correct attribute name for Paper 1.21+
                if (livingEntity.getAttribute(Attribute.MOVEMENT_SPEED) != null) {
                    double speedValue = boss.getSpeed() * 0.1; // Normalize speed value
                    livingEntity.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(speedValue);
                    plugin.getLogger().info("Set speed for " + boss.getName() + " to " + speedValue);
                }

                // Set attack damage if applicable
                if (livingEntity.getAttribute(Attribute.ATTACK_DAMAGE) != null) {
                    livingEntity.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(boss.getDamage());
                    plugin.getLogger().info("Set attack damage for " + boss.getName() + " to " + boss.getDamage());
                }

                // Apply effects
                plugin.getEffectManager().applyBossEffects(livingEntity, boss);

                plugin.getLogger().info("Successfully spawned boss: " + boss.getName());
            }

            spawnedBosses.put(entity.getUniqueId(), bossId);
            return entity;

        } catch (Exception e) {
            plugin.getLogger().severe("Failed to spawn boss " + bossId + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String getBossId(UUID entityId) {
        return spawnedBosses.get(entityId);
    }

    public Boss getBoss(String id) {
        return bosses.get(id);
    }

    public Map<String, Boss> getBosses() {
        return new HashMap<>(bosses);
    }

    public void removeBoss(UUID entityId) {
        spawnedBosses.remove(entityId);
    }

    // Additional utility methods
    public void spawnBossAt(String bossId, Location location) {
        Boss boss = bosses.get(bossId);
        if (boss == null) return;

        // Create a temporary boss with new location
        Boss tempBoss = new Boss(
                boss.getId(),
                boss.getName(),
                boss.getEntityType().name(),
                boss.getHealth(),
                boss.getDamage(),
                boss.getSpeed(),
                null, // effects will be copied separately
                location,
                boss.getSpawnRadius(),
                boss.getDropArtifacts()
        );

        // Temporarily store and spawn
        String tempId = bossId + "_temp_" + System.currentTimeMillis();
        bosses.put(tempId, tempBoss);
        Entity entity = spawnBoss(tempId);
        bosses.remove(tempId); // Clean up temporary boss

        if (entity != null) {
            spawnedBosses.put(entity.getUniqueId(), bossId); // Use original boss ID for tracking
        }
    }

    public void despawnBoss(UUID entityId) {
        String bossId = getBossId(entityId);
        if (bossId != null) {
            Entity entity = Bukkit.getEntity(entityId);
            if (entity != null) {
                entity.remove();
            }
            removeBoss(entityId);
            plugin.getLogger().info("Despawned boss: " + bossId);
        }
    }

    public void despawnAllBosses() {
        for (UUID entityId : new HashMap<>(spawnedBosses).keySet()) {
            despawnBoss(entityId);
        }
        plugin.getLogger().info("Despawned all bosses");
    }
}