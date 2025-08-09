package artPlugin.managers;

import artPlugin.Main;
import artPlugin.models.Artifact;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;

public class ArtifactManager {
    private final Main plugin;
    private final Map<String, Artifact> artifacts = new HashMap<>();
    private final Map<UUID, Set<String>> playerActiveArtifacts = new HashMap<>();

    public ArtifactManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadArtifacts() {
        artifacts.clear();
        File folder = new File(plugin.getDataFolder(), "artifacts");

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

                Artifact artifact = new Artifact(
                        id,
                        config.getString("name", "Unknown Artifact"),
                        config.getString("material", "STONE"),
                        config.getStringList("description"),
                        config.getConfigurationSection("effects"),
                        config.getString("trigger", "ALWAYS")
                );

                artifacts.put(id, artifact);
                plugin.getLogger().info("Loaded artifact: " + artifact.getName());
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load artifact from " + file.getName() + ": " + e.getMessage());
            }
        }

        plugin.getLogger().info("Loaded " + artifacts.size() + " artifacts total.");
    }

    public void giveArtifactToPlayer(Player player, String artifactId) {
        Artifact artifact = artifacts.get(artifactId);
        if (artifact == null) {
            player.sendMessage("§cArtifact not found: " + artifactId);
            return;
        }

        ItemStack item = artifact.createItemStack();
        player.getInventory().addItem(item);
        player.sendMessage("§aYou received: " + artifact.getName());
    }

    public void activateArtifactForPlayer(Player player, String artifactId) {
        Set<String> active = playerActiveArtifacts.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>());
        active.add(artifactId);

        Artifact artifact = artifacts.get(artifactId);
        if (artifact != null) {
            plugin.getEffectManager().applyArtifactEffects(player, artifact);
        }
    }

    public void deactivateArtifactForPlayer(Player player, String artifactId) {
        Set<String> active = playerActiveArtifacts.get(player.getUniqueId());
        if (active != null) {
            active.remove(artifactId);
            plugin.getEffectManager().removeArtifactEffects(player, artifactId);
        }
    }

    public boolean isArtifactActive(Player player, String artifactId) {
        Set<String> active = playerActiveArtifacts.get(player.getUniqueId());
        return active != null && active.contains(artifactId);
    }

    public Map<String, Artifact> getArtifacts() {
        return new HashMap<>(artifacts);
    }

    public Artifact getArtifact(String id) {
        return artifacts.get(id);
    }
}