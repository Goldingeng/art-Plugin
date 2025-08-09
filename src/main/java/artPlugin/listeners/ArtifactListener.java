package artPlugin.listeners;

import artPlugin.Main;
import artPlugin.models.Artifact;
import artPlugin.models.Boss;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class ArtifactListener implements Listener {
    private final Main plugin;
    private final Random random = new Random();

    public ArtifactListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());

        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                String displayName = meta.getDisplayName();

                // Check if this is an artifact
                for (Artifact artifact : plugin.getArtifactManager().getArtifacts().values()) {
                    if (displayName.equals("§6" + artifact.getName())) {
                        plugin.getArtifactManager().activateArtifactForPlayer(player, artifact.getId());
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        String bossId = plugin.getBossManager().getBossId(entity.getUniqueId());

        if (bossId != null) {
            Boss boss = plugin.getBossManager().getBoss(bossId);
            if (boss != null && boss.getDropArtifacts() != null) {
                // Drop artifacts
                for (String artifactId : boss.getDropArtifacts()) {
                    if (random.nextInt(100) < 50) { // 50% chance
                        Artifact artifact = plugin.getArtifactManager().getArtifact(artifactId);
                        if (artifact != null) {
                            event.getDrops().add(artifact.createItemStack());
                        }
                    }
                }
            }
            plugin.getBossManager().removeBoss(entity.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Update artifact effects for player on join
        Player player = event.getPlayer();
        for (int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasDisplayName()) {
                    String displayName = meta.getDisplayName();

                    for (Artifact artifact : plugin.getArtifactManager().getArtifacts().values()) {
                        if (displayName.equals("§6" + artifact.getName())) {
                            plugin.getArtifactManager().activateArtifactForPlayer(player, artifact.getId());
                            break;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Clean up player data
        Player player = event.getPlayer();
        // Remove all active artifacts for this player to prevent memory leaks
    }
}
