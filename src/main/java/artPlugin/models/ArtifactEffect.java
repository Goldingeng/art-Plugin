package artPlugin.models;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArtifactEffect {
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

    public String getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public int getChance() {
        return chance;
    }

    public String getPeriod() {
        return period;
    }

    public int getDuration() {
        return duration;
    }

    /**
     * Проверяет, должен ли эффект сработать на основе шанса
     */
    public boolean shouldTrigger() {
        return Math.random() * 100 < chance;
    }

    /**
     * Создает PotionEffect на основе данных
     */
    public PotionEffect toPotionEffect() {
        PotionEffectType effectType = PotionEffectType.getByName(type.toUpperCase());
        if (effectType == null) {
            return null;
        }

        // Конвертируем уровень (в конфиге 1-3, в игре 0-2)
        int potionLevel = Math.max(0, level - 1);

        // Продолжительность в тиках (20 тиков = 1 секунда)
        int durationTicks = duration * 20;

        return new PotionEffect(effectType, durationTicks, potionLevel, false, false);
    }

    /**
     * Проверяет, является ли эффект кастомным (не ванильным)
     */
    public boolean isCustomEffect() {
        return PotionEffectType.getByName(type.toUpperCase()) == null;
    }
}