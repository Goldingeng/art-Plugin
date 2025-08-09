package artPlugin.managers;

import artPlugin.Main;
import artPlugin.Artifact;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ArtifactManager {
    private final Main plugin;
    private final Map<String, Artifact> artifacts = new HashMap<>();

    public ArtifactManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadArtifacts() {
        artifacts.clear();
        File folder = new File(plugin.getDataFolder(), "artifacts");
        for (File file : folder.listFiles()) {
            if (!file.getName().endsWith(".yml")) continue;
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            Artifact artifact = new Artifact(
                    config.getString("name"),
                    config.getString("material"),
                    config.getStringList("description"),
                    config.getConfigurationSection("effects"),
                    config.getString("trigger")
            );
            artifacts.put(artifact.getName(), artifact);
            plugin.getLogger().info("Loaded artifact: " + artifact.getName());
        }
    }

    public Map<String, Artifact> getArtifacts() {
        return artifacts;
    }
}
