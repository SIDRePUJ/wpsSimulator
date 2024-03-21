package org.wpsim.PeasantFamily.Emotions;

import BESA.Emotional.EmotionalConfig;

public enum EmotionalEventType {
    PLANTING("PLANTING", EmotionalConfig.Events.Desirable),
    PLANTINGFAILED("PLANTINGFAILED", EmotionalConfig.Events.Undesirable),
    HOUSEHOLDING("HOUSEHOLDING", EmotionalConfig.Events.Desirable),
    IRRIGATING("IRRIGATING", EmotionalConfig.Events.SomewhatDesirable),
    HARVESTING("HARVESTING", EmotionalConfig.Events.Desirable),
    SELLING("SELLING", EmotionalConfig.Events.Desirable),
    WORK("WORK", EmotionalConfig.Events.SomewhatDesirable),
    BUYING("BUYING", EmotionalConfig.Events.SomewhatDesirable),
    DOVITALS("DOVITALS", EmotionalConfig.Events.Desirable),
    STARVING("STARVING", EmotionalConfig.Events.Undesirable),
    THIEVING("THIEVING", EmotionalConfig.Events.Undesirable),
    HELPED("HELPED", EmotionalConfig.Events.Undesirable),
    CROPDISEASES("CROP_DISEASES", EmotionalConfig.Events.Undesirable),
    CHECKCROPS("CHECKCROPS", EmotionalConfig.Events.SomewhatDesirable),
    LEISURE("LEISURE", EmotionalConfig.Events.SomewhatDesirable);

    private final String emoType;
    private final EmotionalConfig.Events config;

    private EmotionalEventType(String emoType, EmotionalConfig.Events config) {
        this.emoType = emoType;
        this.config = config;
    }

    public static EmotionalEventType getFromId(String ident) {
        EmotionalEventType ret = null;
        for (EmotionalEventType sdt : values()) {
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

    public EmotionalConfig.Events getConfigEnum() {
        return config;
    }

    public String getConfig() {
        return config.toString();
    }

}
