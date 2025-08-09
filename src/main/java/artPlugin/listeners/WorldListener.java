package artPlugin.listeners;

import artPlugin.Main;
import artPlugin.models.Artifact;
import artPlugin.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.NamespacedKey;

import java.util.Random;

public class WorldListener implements Listener {
    private final Main plugin;
    private final Random random = new Random();
    private final NamespacedKey artifactKey;

    public WorldListener(Main plugin) {
        this.plugin = plugin;
        this.artifactKey = new NamespacedKey(plugin, "artifact_id");
    }

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        // Add artifacts to naturally generated loot
        String locationType = getLocationType(event);
        if (locationType == null) return;

        int chance = plugin.getConfigManager().getSpawnChance(locationType);
        if (random.nextInt(100) < chance) {
            Artifact artifact = getRandomArtifact();
            if (artifact != null) {
                ItemStack artifactItem = ItemUtils.createArtifactItem(artifact, artifactKey);
                event.getLoot().add(artifactItem);

                if (plugin.getConfigManager().isDebugEnabled()) {
                    plugin.getLogger().info("Added artifact " + artifact.getName() + " to loot table at " + locationType);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        // Handle chest opening for custom artifact spawning
        if (event.getInventory().getHolder() instanceof Chest) {
            Chest chest = (Chest) event.getInventory().getHolder();
            // Custom logic for adding artifacts to existing chests
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Handle spawner breaking for artifact drops
        if (event.getBlock().getType() == Material.SPAWNER) {
            int chance = plugin.getConfigManager().getSpawnChance("spawner_break");
            if (random.nextInt(100) < chance) {
                Artifact artifact = getRandomArtifact();
                if (artifact != null) {
                    ItemStack artifactItem = ItemUtils.createArtifactItem(artifact, artifactKey);
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), artifactItem);
                }
            }
        }
    }

    private String getLocationType(LootGenerateEvent event) {
        String lootTable = event.getLootTable().getKey().getKey();

        if (lootTable.contains("nether_bridge")) return "nether_fortress";
        if (lootTable.contains("bastion")) return "bastion_remnant";
        if (lootTable.contains("end_city")) return "end_city";
        if (lootTable.contains("village")) return "village_chest";
        if (lootTable.contains("desert_pyramid") || lootTable.contains("jungle_temple")) return "temple_chest";
        if (lootTable.contains("abandoned_mineshaft")) return "abandoned_mineshaft";
        if (lootTable.contains("stronghold")) return "stronghold";
        if (lootTable.contains("underwater_ruin")) return "ocean_ruin";
        if (lootTable.contains("shipwreck")) return "shipwreck";

        return null;
    }

    private Artifact getRandomArtifact() {
        var artifacts = plugin.getArtifactManager().getArtifacts().values().toArray(new Artifact[0]);
        if (artifacts.length == 0) return null;
        return artifacts[random.nextInt(artifacts.length)];
    }
}
