package com.cyberspectraa.cyberquest.quest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public record QuestStageDefinition(
    String id,
    String title,
    String lead,
    List<String> notes,
    List<QuestObjectiveDefinition> objectives
) {
    public QuestStageDefinition {
        id = id == null || id.isBlank() ? "stage" : id.trim();
        title = title == null ? "" : title.trim();
        lead = lead == null ? "" : lead.trim();
        notes = notes == null ? List.of() : List.copyOf(notes);
        objectives = objectives == null ? List.of() : List.copyOf(objectives);
    }

    public static QuestStageDefinition parse(
        JsonObject json,
        int index
    ) {
        String id = string(json, "id", "stage_" + index);
        String title = string(json, "title", "");
        String lead = string(json, "lead", "");
        List<String> notes = stringList(json, "notes");

        List<QuestObjectiveDefinition> objectives =
            new ArrayList<>();

        if (json.has("objectives")
                && json.get("objectives").isJsonArray()) {
            int objectiveIndex = 0;

            for (JsonElement element
                    : json.getAsJsonArray("objectives")) {
                if (!element.isJsonObject()) {
                    continue;
                }

                objectives.add(
                    QuestObjectiveDefinition.parse(
                        element.getAsJsonObject(),
                        objectiveIndex++
                    )
                );
            }
        }

        return new QuestStageDefinition(
            id,
            title,
            lead,
            notes,
            objectives
        );
    }

    private static List<String> stringList(
        JsonObject json,
        String key
    ) {
        List<String> values = new ArrayList<>();

        if (!json.has(key)
                || !json.get(key).isJsonArray()) {
            return values;
        }

        for (JsonElement element
                : json.getAsJsonArray(key)) {
            if (!element.isJsonPrimitive()) {
                continue;
            }

            String value = element.getAsString().trim();

            if (!value.isBlank()) {
                values.add(value);
            }
        }

        return values;
    }

    private static String string(
        JsonObject json,
        String key,
        String fallback
    ) {
        return json.has(key)
                && json.get(key).isJsonPrimitive()
            ? json.get(key).getAsString()
            : fallback;
    }
}
