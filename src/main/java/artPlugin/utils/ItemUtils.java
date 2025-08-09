package artPlugin.utils;

import artPlugin.models.Artifact;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

public class ItemUtils {

    public static boolean isArtifact(ItemStack item, NamespacedKey artifactKey) {
        if (item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(artifactKey, PersistentDataType.STRING);
    }

    public static String getArtifactId(ItemStack item, NamespacedKey artifactKey) {
        if (!isArtifact(item, artifactKey)) return null;

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(artifactKey, PersistentDataType.STRING);
    }

    public static ItemStack createArtifactItem(Artifact artifact, NamespacedKey artifactKey) {
        ItemStack item = artifact.createItemStack();
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.getPersistentDataContainer().set(artifactKey, PersistentDataType.STRING, artifact.getId());
            item.setItemMeta(meta);
        }

        return item;
    }

    public static boolean compareItems(ItemStack item1, ItemStack item2) {
        if (item1 == null && item2 == null) return true;
        if (item1 == null || item2 == null) return false;

        if (!item1.getType().equals(item2.getType())) return false;
        if (!item1.hasItemMeta() && !item2.hasItemMeta()) return true;
        if (item1.hasItemMeta() != item2.hasItemMeta()) return false;

        ItemMeta meta1 = item1.getItemMeta();
        ItemMeta meta2 = item2.getItemMeta();

        return meta1.equals(meta2);
    }
}
