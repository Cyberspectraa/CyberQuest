package com.cyberspectraa.cyberquest.quest;

import java.util.Locale;

public enum QuestObjectiveType {
    KILL,
    COLLECT,
    TALK,
    DELIVER_ITEM,
    VISIT,
    ADVANCEMENT;

    public static QuestObjectiveType fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Quest objective type is required");
        }

        return valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
