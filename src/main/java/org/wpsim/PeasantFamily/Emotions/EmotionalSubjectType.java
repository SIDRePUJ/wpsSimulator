package org.wpsim.PeasantFamily.Emotions;

import BESA.Emotional.EmotionalConfig;

public enum EmotionalSubjectType {

    FAMILY("FAMILY", EmotionalConfig.People.Close),
    FRIEND("FRIEND", EmotionalConfig.People.Friend),
    NEIGHBOR("NEIGHBOR", EmotionalConfig.People.Friend),
    STRANGER("STRANGER", EmotionalConfig.People.Stranger),
    THIEF("THIEF",EmotionalConfig.People.Enemy);

    private final String emoType;
    private EmotionalConfig.People config;

    private EmotionalSubjectType(String emoType,EmotionalConfig.People config) {
        this.emoType = emoType;
        this.config = config;
    }

    public EmotionalConfig.People getConfigEnum() {
        return config;
    }

    public String getConfig() {
        return config.toString();
    }

    public static EmotionalSubjectType getFromId(String ident) {
        EmotionalSubjectType ret = null;
        for (EmotionalSubjectType sdt : values()) {
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
}

