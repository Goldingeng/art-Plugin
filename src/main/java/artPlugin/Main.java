package artPlugin;

import artPlugin.commands.ArtifactCommand;
import artPlugin.listeners.ArtifactListener;
import artPlugin.listeners.WorldListener;
import artPlugin.managers.ArtifactManager;
import artPlugin.managers.BossManager;
import artPlugin.managers.EffectManager;
import artPlugin.utils.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    private ArtifactManager artifactManager;
    private BossManager bossManager;
    private EffectManager effectManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        // Initialize configuration first
        configManager = new ConfigManager(this);

        // Create default folders and files
        createDefaultFolders();
        createExampleFiles();

        // Initialize managers
        effectManager = new EffectManager(this);
        artifactManager = new ArtifactManager(this);
        bossManager = new BossManager(this);

        // Load configurations
        artifactManager.loadArtifacts();
        bossManager.loadBosses();

        // Register listeners
        getServer().getPluginManager().registerEvents(new ArtifactListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldListener(this), this);

        // Register commands
        getCommand("artifact").setExecutor(new ArtifactCommand(this));

        // Start periodic tasks
        startPeriodicTasks();

        getLogger().info("Artifact Plugin v1.0 enabled successfully!");
        getLogger().info("Loaded " + artifactManager.getArtifacts().size() + " artifacts and " +
                bossManager.getBosses().size() + " bosses");
    }

    @Override
    public void onDisable() {
        if (effectManager != null) {
            effectManager.cleanup();
        }
        getLogger().info("Artifact Plugin disabled!");
    }

    private void createDefaultFolders() {
        new File(getDataFolder(), "artifacts").mkdirs();
        new File(getDataFolder(), "bosses").mkdirs();
    }

    private void createExampleFiles() {
        // Create example files if they don't exist
        String[] artifactFiles = {"speed_boots.yml", "fire_sword.yml", "healing_amulet.yml", "shadow_cloak.yml"};
        String[] bossFiles = {"fire_lord.yml", "shadow_assassin.yml", "ancient_guardian.yml"};

        for (String file : artifactFiles) {
            File artifactFile = new File(getDataFolder(), "artifacts/" + file);
            if (!artifactFile.exists()) {
                saveResource("artifacts/" + file, false);
            }
        }

        for (String file : bossFiles) {
            File bossFile = new File(getDataFolder(), "bosses/" + file);
            if (!bossFile.exists()) {
                saveResource("bosses/" + file, false);
            }
        }
    }

    private void startPeriodicTasks() {
        // Auto-save task
        if (configManager.getConfig().getBoolean("settings.auto_save", true)) {
            int interval = configManager.getConfig().getInt("settings.save_interval", 300);
            getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
                // Save player data, update statistics, etc.
                if (configManager.isDebugEnabled()) {
                    getLogger().info("Auto-save completed");
                }
            }, interval * 20L, interval * 20L);
        }

        // Artifact effect update task
        getServer().getScheduler().runTaskTimer(this, () -> {
            effectManager.updatePeriodicEffects();
        }, 20L, 20L); // Run every second
    }

    // Getters
    public ArtifactManager getArtifactManager() { return artifactManager; }
    public BossManager getBossManager() { return bossManager; }
    public EffectManager getEffectManager() { return effectManager; }
    public ConfigManager getConfigManager() { return configManager; }
}