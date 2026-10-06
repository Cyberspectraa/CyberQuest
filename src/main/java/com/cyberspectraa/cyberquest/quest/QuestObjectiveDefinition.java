package com.cyberspectraa.cyberquest.quest;

import com.google.gson.JsonObject;

public record QuestObjectiveDefinition(
    String id,
    QuestObjectiveType type,
    String description,
    String target,
    int count,
    String dimension,
    double x,
    double y,
    double z,
    double radius
) {
    public QuestObjectiveDefinition {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Quest objective id is required");
        }
        if (type == null) {
            throw new IllegalArgumentException("Quest objective type is required");
        }
        description = description == null ? "" : description;
        target = target == null ? "" : target;
        count = Math.max(1, count);
        dimension = dimension == null ? "" : dimension;
        radius = Math.max(0.5D, radius);
    }

    public static QuestObjectiveDefinition parse(JsonObject json, int index) {
        String id = string(json, "id", "objective_" + index);
        QuestObjectiveType type = QuestObjectiveType.fromString(
            string(json, "type", "")
        );
        String description = string(json, "description", "");
        String target = string(json, "target", "");
        int count = integer(json, "count", 1);
        String dimension = string(json, "dimension", "");
        double x = decimal(json, "x", 0.0D);
        double y = decimal(json, "y", 0.0D);
        double z = decimal(json, "z", 0.0D);
        double radius = decimal(json, "radius", 5.0D);

        return new QuestObjectiveDefinition(
            id,
            type,
            description,
            target,
            count,
            dimension,
            x,
            y,
            z,
            radius
        );
    }

    public String displayText() {
        if (!description.isBlank()) {
            return description;
        }

        return switch (type) {
            case KILL -> "Defeat " + count + " " + target;
            case COLLECT -> "Collect " + count + " " + target;
            case TALK -> "Talk to " + target;
            case VISIT -> "Visit the marked location";
            case ADVANCEMENT -> "Earn advancement " + target;
        };
    }

    private static String string(
        JsonObject json,
        String key,
        String fallback
    ) {
        return json.has(key) && json.get(key).isJsonPrimitive()
            ? json.get(key).getAsString()
            : fallback;
    }

    private static int integer(
        JsonObject json,
        String key,
        int fallback
    ) {
        return json.has(key) && json.get(key).isJsonPrimitive()
            ? json.get(key).getAsInt()
            : fallback;
    }

    private static double decimal(
        JsonObject json,
        String key,
        double fallback
    ) {
        return json.has(key) && json.get(key).isJsonPrimitive()
            ? json.get(key).getAsDouble()
            : fallback;
    }
}
