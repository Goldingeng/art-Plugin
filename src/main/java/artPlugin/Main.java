package artPlugin;

import artPlugin.managers.ArtifactManager;
import artPlugin.managers.BossManager;
import artPlugin.managers.TriggerManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    private ArtifactManager artifactManager;
    private BossManager bossManager;
    private TriggerManager triggerManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        createDefaultFolders();

        artifactManager = new ArtifactManager(this);
        bossManager = new BossManager(this);
        triggerManager = new TriggerManager(this);

        artifactManager.loadArtifacts();
        bossManager.loadBosses();
        triggerManager.loadTriggers();

        getLogger().info("MyPlugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MyPlugin disabled!");
    }

    private void createDefaultFolders() {
        new File(getDataFolder(), "artifacts").mkdirs();
        new File(getDataFolder(), "bosses").mkdirs();
        new File(getDataFolder(), "triggers").mkdirs();
    }

    public ArtifactManager getArtifactManager() { return artifactManager; }
    public BossManager getBossManager() { return bossManager; }
    public TriggerManager getTriggerManager() { return triggerManager; }
}
