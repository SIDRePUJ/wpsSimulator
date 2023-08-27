package org.wpsim.PeasantFamily.Emotions;

import BESA.Emotional.EmotionalConfig;

public enum EmotionalObjectType {

    MONEY("MONEY", EmotionalConfig.Objects.Valuable),
    FOOD("FOOD", EmotionalConfig.Objects.Important),
    WATER("WATER", EmotionalConfig.Objects.Neutral),
    SEEDS("SEEDS", EmotionalConfig.Objects.Valuable),
    NULL("", EmotionalConfig.Objects.Neutral);

    private final String emoType;
    private final EmotionalConfig.Objects config;

    private EmotionalObjectType(String emoType, EmotionalConfig.Objects config) {
        this.emoType = emoType;
        this.config = config;
    }

    public static EmotionalObjectType getFromId(String ident) {
        EmotionalObjectType ret = NULL;
        for (EmotionalObjectType sdt : values()) {
            if (sdt.emoType.equalsIgnoreCase(ident)) {
                ret = sdt;
                break;
            }
        }
        return ret;
    }

    public String getEmoType() {
        return emoType;
    }

    public EmotionalConfig.Objects getConfigEnum() {
        return config;
    }

    public String getConfig() {
        return config.toString();
    }
}
