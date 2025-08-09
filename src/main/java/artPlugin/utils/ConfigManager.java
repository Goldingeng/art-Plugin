package artPlugin.utils;

import artPlugin.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private final Main plugin;
    private YamlConfiguration config;
    private File configFile;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save config.yml: " + e.getMessage());
        }
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public String getMessage(String key, String defaultValue) {
        return config.getString("messages." + key, defaultValue).replace('&', '§');
    }

    public int getSpawnChance(String location) {
        return config.getInt("spawn_chances." + location, 0);
    }

    public boolean isDebugEnabled() {
        return config.getBoolean("settings.debug", false);
    }
}