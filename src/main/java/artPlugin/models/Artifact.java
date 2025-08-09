package artPlugin.models;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Artifact {
    private final String id;
    private final String name;
    private final Material material;
    private final List<String> description;
    private final Map<String, ArtifactEffect> effects;
    private final String trigger;

    public Artifact(String id, String name, String material, List<String> description,
                    ConfigurationSection effectsSection, String trigger) {
        this.id = id;
        this.name = name;
        this.material = Material.valueOf(material.toUpperCase());
        this.description = description;
        this.trigger = trigger != null ? trigger : "ALWAYS";
        this.effects = new HashMap<>();

        if (effectsSection != null) {
            for (String key : effectsSection.getKeys(false)) {
                ConfigurationSection effect = effectsSection.getConfigurationSection(key);
                if (effect != null) {
                    this.effects.put(key, new ArtifactEffect(
                            effect.getString("type", "SPEED"),
                            effect.getInt("level", 1),
                            effect.getInt("chance", 100),
                            effect.getString("period", "permanent"),
                            effect.getInt("duration", 200)
                    ));
                }
            }
        }
    }

    public ItemStack createItemStack() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§6" + name);
            meta.setLore(description);
            item.setItemMeta(meta);
        }

        return item;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Material getMaterial() { return material; }
    public List<String> getDescription() { return description; }
    public Map<String, ArtifactEffect> getEffects() { return effects; }
    public String getTrigger() { return trigger; }

    public static class ArtifactEffect {
        private final String type;
        private final int level;
        private final int chance;
        private final String period;
        private final int duration;

        public ArtifactEffect(String type, int level, int chance, String period, int duration) {
            this.type = type;
            this.level = level;
            this.chance = chance;
            this.period = period;
            this.duration = duration;
        }

        public String getType() { return type; }
        public int getLevel() { return level; }
        public int getChance() { return chance; }
        public String getPeriod() { return period; }
        public int getDuration() { return duration; }
    }
}
