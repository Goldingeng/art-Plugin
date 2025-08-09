package artPlugin.models;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Boss {
    private final String id;
    private final String name;
    private final EntityType entityType;
    private final double health;
    private final double damage;
    private final double speed;
    private final Map<String, Artifact.ArtifactEffect> effects;
    private final Location spawnLocation;
    private final int spawnRadius;
    private final List<String> dropArtifacts;

    public Boss(String id, String name, String entityType, double health, double damage,
                double speed, ConfigurationSection effectsSection, Location spawnLocation,
                int spawnRadius, List<String> dropArtifacts) {
        this.id = id;
        this.name = name;
        this.entityType = EntityType.valueOf(entityType.toUpperCase());
        this.health = health;
        this.damage = damage;
        this.speed = speed;
        this.spawnLocation = spawnLocation;
        this.spawnRadius = spawnRadius;
        this.dropArtifacts = dropArtifacts;
        this.effects = new HashMap<>();

        if (effectsSection != null) {
            for (String key : effectsSection.getKeys(false)) {
                ConfigurationSection effect = effectsSection.getConfigurationSection(key);
                if (effect != null) {
                    this.effects.put(key, new Artifact.ArtifactEffect(
                            effect.getString("type", "SPEED"),
                            effect.getInt("level", 1),
                            effect.getInt("chance", 100),
                            effect.getString("period", "permanent"),
                            effect.getInt("duration", -1)
                    ));
                }
            }
        }
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public EntityType getEntityType() { return entityType; }
    public double getHealth() { return health; }
    public double getDamage() { return damage; }
    public double getSpeed() { return speed; }
    public Map<String, Artifact.ArtifactEffect> getEffects() { return effects; }
    public Location getSpawnLocation() { return spawnLocation; }
    public int getSpawnRadius() { return spawnRadius; }
    public List<String> getDropArtifacts() { return dropArtifacts; }
}