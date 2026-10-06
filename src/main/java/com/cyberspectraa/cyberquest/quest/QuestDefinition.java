package com.cyberspectraa.cyberquest.quest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public record QuestDefinition(
    ResourceLocation id,
    String title,
    String description,
    String category,
    boolean repeatable,
    boolean autoTurnIn,
    List<ResourceLocation> prerequisites,
    int minCyberLevel,
    int minStoryAct,
    List<String> requiredWorldFlags,
    List<String> requiredUnlockedRegions,
    String requiredRace,
    String requiredEvolution,
    String requiredClass,
    String requiredClassAdvancement,
    List<QuestObjectiveDefinition> objectives,
    QuestReward reward
) {
    public QuestDefinition {
        title = title == null || title.isBlank()
            ? id.toString()
            : title;
        description = description == null
            ? ""
            : description;
        category = category == null || category.isBlank()
            ? "side"
            : category;
        prerequisites = prerequisites == null
            ? List.of()
            : List.copyOf(prerequisites);
        minCyberLevel = Math.max(1, minCyberLevel);
        minStoryAct = Math.max(1, minStoryAct);
        requiredWorldFlags = normalizedList(
            requiredWorldFlags
        );
        requiredUnlockedRegions = normalizedList(
            requiredUnlockedRegions
        );
        requiredRace = normalize(requiredRace);
        requiredEvolution = normalize(requiredEvolution);
        requiredClass = normalize(requiredClass);
        requiredClassAdvancement = normalize(
            requiredClassAdvancement
        );
        objectives = objectives == null
            ? List.of()
            : List.copyOf(objectives);
        reward = reward == null
            ? QuestReward.empty()
            : reward;
    }

    public static QuestDefinition parse(
        ResourceLocation id,
        JsonObject json
    ) {
        String title = string(
            json,
            "title",
            id.toString()
        );
        String description = string(
            json,
            "description",
            ""
        );
        String category = string(
            json,
            "category",
            "side"
        );
        boolean repeatable = bool(
            json,
            "repeatable",
            false
        );
        boolean autoTurnIn = bool(
            json,
            "auto_turn_in",
            false
        );
        int minCyberLevel = integer(
            json,
            "min_cyber_level",
            1
        );
        int minStoryAct = integer(
            json,
            "min_story_act",
            1
        );

        List<String> requiredWorldFlags =
            stringList(
                json,
                "required_world_flags"
            );

        List<String> requiredUnlockedRegions =
            stringList(
                json,
                "required_unlocked_regions"
            );

        String requiredRace = string(
            json,
            "required_race",
            ""
        );
        String requiredEvolution = string(
            json,
            "required_evolution",
            ""
        );
        String requiredClass = string(
            json,
            "required_class",
            ""
        );
        String requiredClassAdvancement = string(
            json,
            "required_class_advancement",
            ""
        );

        List<ResourceLocation> prerequisites =
            new ArrayList<>();

        if (json.has("prerequisites")
                && json.get("prerequisites")
                    .isJsonArray()) {
            for (JsonElement element
                    : json.getAsJsonArray(
                        "prerequisites"
                    )) {
                if (!element.isJsonPrimitive()) {
                    continue;
                }

                ResourceLocation questId =
                    ResourceLocation.tryParse(
                        element.getAsString()
                    );

                if (questId != null) {
                    prerequisites.add(questId);
                }
            }
        }

        List<QuestObjectiveDefinition> objectives =
            new ArrayList<>();

        if (json.has("objectives")
                && json.get("objectives")
                    .isJsonArray()) {
            JsonArray array =
                json.getAsJsonArray("objectives");
            int index = 0;

            for (JsonElement element : array) {
                if (!element.isJsonObject()) {
                    continue;
                }

                objectives.add(
                    QuestObjectiveDefinition.parse(
                        element.getAsJsonObject(),
                        index++
                    )
                );
            }
        }

        QuestReward reward =
            json.has("rewards")
                && json.get("rewards")
                    .isJsonObject()
            ? QuestReward.parse(
                json.getAsJsonObject("rewards")
            )
            : QuestReward.empty();

        return new QuestDefinition(
            id,
            title,
            description,
            category,
            repeatable,
            autoTurnIn,
            prerequisites,
            minCyberLevel,
            minStoryAct,
            requiredWorldFlags,
            requiredUnlockedRegions,
            requiredRace,
            requiredEvolution,
            requiredClass,
            requiredClassAdvancement,
            objectives,
            reward
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

            String value = normalize(
                element.getAsString()
            );

            if (!value.isBlank()) {
                values.add(value);
            }
        }

        return values;
    }

    private static List<String> normalizedList(
        List<String> values
    ) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }

        List<String> result = new ArrayList<>();

        for (String value : values) {
            String normalized = normalize(value);

            if (!normalized.isBlank()) {
                result.add(normalized);
            }
        }

        return List.copyOf(result);
    }

    private static String normalize(String value) {
        return value == null
            ? ""
            : value.trim().toLowerCase(Locale.ROOT);
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

    private static int integer(
        JsonObject json,
        String key,
        int fallback
    ) {
        return json.has(key)
                && json.get(key).isJsonPrimitive()
            ? json.get(key).getAsInt()
            : fallback;
    }

    private static boolean bool(
        JsonObject json,
        String key,
        boolean fallback
    ) {
        return json.has(key)
                && json.get(key).isJsonPrimitive()
            ? json.get(key).getAsBoolean()
            : fallback;
    }
}
