package artPlugin.models;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class Artifact {
    private final String name;
    private final String material;
    private final List<String> description;
    private final ConfigurationSection effects;
    private final String trigger;

    public Artifact(String name, String material, List<String> description, ConfigurationSection effects, String trigger) {
        this.name = name;
        this.material = material;
        this.description = description;
        this.effects = effects;
        this.trigger = trigger;
    }

    public String getName() { return name; }
    public String getMaterial() { return material; }
    public List<String> getDescription() { return description; }
    public ConfigurationSection getEffects() { return effects; }
    public String getTrigger() { return trigger; }
}
