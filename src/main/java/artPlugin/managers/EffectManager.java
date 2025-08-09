package artPlugin.managers;

import artPlugin.Main;
import artPlugin.models.Artifact;
import artPlugin.models.Boss;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Random;
import java.util.Iterator;

public class EffectManager {
    private final Main plugin;
    private final Map<UUID, Map<String, BukkitTask>> activeTasks = new HashMap<>();
    private final Map<UUID, Map<String, Long>> lastEffectTime = new HashMap<>();
    private final Random random = new Random();

    public EffectManager(Main plugin) {
        this.plugin = plugin;
    }

    public void applyArtifactEffects(Player player, Artifact artifact) {
        for (Map.Entry<String, Artifact.ArtifactEffect> entry : artifact.getEffects().entrySet()) {
            String effectId = entry.getKey();
            Artifact.ArtifactEffect effect = entry.getValue();

            applyEffect(player, artifact.getId() + "_" + effectId, effect);
        }
    }

    public void applyBossEffects(LivingEntity entity, Boss boss) {
        for (Map.Entry<String, Artifact.ArtifactEffect> entry : boss.getEffects().entrySet()) {
            String effectId = entry.getKey();
            Artifact.ArtifactEffect effect = entry.getValue();

            applyEffect(entity, boss.getId() + "_" + effectId, effect);
        }
    }

    private void applyEffect(LivingEntity entity, String effectId, Artifact.ArtifactEffect effect) {
        // Check chance
        if (random.nextInt(100) >= effect.getChance()) {
            return;
        }

        PotionEffectType potionType = getPotionEffectType(effect.getType());
        if (potionType == null) {
            handleCustomEffect(entity, effectId, effect);
            return;
        }

        int duration = effect.getDuration();
        if (effect.getPeriod().equals("permanent")) {
            duration = Integer.MAX_VALUE;
        }

        PotionEffect potionEffect = new PotionEffect(potionType, duration, effect.getLevel() - 1);
        entity.addPotionEffect(potionEffect, true);

        // Track effect timing for periodic effects
        if (!effect.getPeriod().equals("permanent")) {
            Map<String, Long> playerTimes = lastEffectTime.computeIfAbsent(entity.getUniqueId(), k -> new HashMap<>());
            playerTimes.put(effectId, System.currentTimeMillis());
        }
    }

    private void handleCustomEffect(LivingEntity entity, String effectId, Artifact.ArtifactEffect effect) {
        // Handle custom effects like lifesteal, magnetic, etc.
        switch (effect.getType().toLowerCase()) {
            case "lifesteal":
                // Implementation would be in combat listener
                break;
            case "magnetic":
                // Implementation would be in item pickup listener
                break;
            case "wither_shield":
                entity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 0, 0), false);
                break;
            // Add more custom effects as needed
        }
    }

    public void updatePeriodicEffects() {
        Iterator<Map.Entry<UUID, Map<String, Long>>> iterator = lastEffectTime.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, Map<String, Long>> entry = iterator.next();
            UUID entityId = entry.getKey();
            Player player = plugin.getServer().getPlayer(entityId);

            if (player == null || !player.isOnline()) {
                iterator.remove();
                continue;
            }

            // Update periodic effects for this player
            // This is where you'd implement timed effect applications
        }
    }

    public void removeArtifactEffects(Player player, String artifactId) {
        Artifact artifact = plugin.getArtifactManager().getArtifact(artifactId);
        if (artifact == null) return;

        for (Artifact.ArtifactEffect effect : artifact.getEffects().values()) {
            PotionEffectType potionType = getPotionEffectType(effect.getType());
            if (potionType != null) {
                player.removePotionEffect(potionType);
            }
        }

        // Remove from tracking
        Map<String, Long> playerTimes = lastEffectTime.get(player.getUniqueId());
        if (playerTimes != null) {
            playerTimes.entrySet().removeIf(entry -> entry.getKey().startsWith(artifactId + "_"));
        }
    }

    private PotionEffectType getPotionEffectType(String effectName) {
        try {
            return PotionEffectType.getByName(effectName.toUpperCase());
        } catch (Exception e) {
            // Try alternative names
            switch (effectName.toLowerCase()) {
                case "haste": return PotionEffectType.MINING_FATIGUE;
                case "mining_fatigue": return PotionEffectType.SLOW_FALLING;
                case "resistance": return PotionEffectType.BAD_OMEN;
                case "jump_boost": case "jump": return PotionEffectType.JUMP_BOOST;
                case "health_boost": return PotionEffectType.HEALTH_BOOST;
                case "instant_damage": return PotionEffectType.HASTE;
                case "instant_health": return PotionEffectType.WITHER;
                default:
                    plugin.getLogger().warning("Unknown potion effect: " + effectName);
                    return null;
            }
        }
    }

    public void cleanup() {
        for (Map<String, BukkitTask> tasks : activeTasks.values()) {
            for (BukkitTask task : tasks.values()) {
                if (task != null && !task.isCancelled()) {
                    task.cancel();
                }
            }
        }
        activeTasks.clear();
        lastEffectTime.clear();
    }
}