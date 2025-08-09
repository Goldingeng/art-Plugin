package artPlugin.models;

public class Trigger {
    private final String name;
    private final String event;
    private final String condition;
    private final int cooldown;

    public Trigger(String name, String event, String condition, int cooldown) {
        this.name = name;
        this.event = event;
        this.condition = condition;
        this.cooldown = cooldown;
    }

    public String getName() {
        return name;
    }

    public String getEvent() {
        return event;
    }

    public String getCondition() {
        return condition;
    }

    public int getCooldown() {
        return cooldown;
    }

    /**
     * Enum для стандартных триггеров
     */
    public enum TriggerType {
        ON_WALK("ON_WALK"),
        ON_ATTACK("ON_ATTACK"),
        ON_DAMAGE_TAKEN("ON_DAMAGE_TAKEN"),
        ON_KILL("ON_KILL"),
        ON_JUMP("ON_JUMP"),
        ON_SPRINT("ON_SPRINT"),
        ON_SNEAK("ON_SNEAK"),
        ON_BLOCK_BREAK("ON_BLOCK_BREAK"),
        ON_BLOCK_PLACE("ON_BLOCK_PLACE"),
        ON_INTERACT("ON_INTERACT"),
        ON_CONSUME("ON_CONSUME"),
        ON_PICKUP("ON_PICKUP"),
        ON_DROP("ON_DROP"),
        ON_FISH("ON_FISH"),
        ON_ENCHANT("ON_ENCHANT"),
        ON_CRAFT("ON_CRAFT"),
        ON_DEATH("ON_DEATH"),
        ON_RESPAWN("ON_RESPAWN"),
        ON_TELEPORT("ON_TELEPORT"),
        ON_ENTER_VEHICLE("ON_ENTER_VEHICLE"),
        ON_EXIT_VEHICLE("ON_EXIT_VEHICLE"),
        CONSTANTLY("CONSTANTLY"),
        PERIODIC("PERIODIC");

        private final String value;

        TriggerType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static TriggerType fromString(String text) {
            for (TriggerType type : TriggerType.values()) {
                if (type.value.equalsIgnoreCase(text)) {
                    return type;
                }
            }
            return null;
        }
    }
}