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
    String journalSection,
    String completionText,
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
    int timeLimitDays,
    int failureGuildReputation,
    List<QuestStageDefinition> stages,
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
            : category.trim().toLowerCase(Locale.ROOT);
        journalSection = normalizeJournalSection(
            journalSection,
            category
        );
        completionText = completionText == null
            ? ""
            : completionText.trim();
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
        timeLimitDays = Math.max(0, timeLimitDays);
        failureGuildReputation = Math.max(
            0,
            failureGuildReputation
        );

        if (stages == null || stages.isEmpty()) {
            stages = List.of(
                new QuestStageDefinition(
                    "main",
                    "",
                    "",
                    List.of(),
                    List.of()
                )
            );
        } else {
            stages = List.copyOf(stages);
        }

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
        String journalSection = string(
            json,
            "journal_section",
            ""
        );
        String completionText = string(
            json,
            "completion_text",
            ""
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
        int timeLimitDays = integer(
            json,
            "time_limit_days",
            0
        );
        int failureGuildReputation = integer(
            json,
            "failure_guild_reputation",
            0
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

        List<QuestStageDefinition> stages =
            new ArrayList<>();

        if (json.has("stages")
                && json.get("stages").isJsonArray()) {
            int stageIndex = 0;

            for (JsonElement element
                    : json.getAsJsonArray("stages")) {
                if (!element.isJsonObject()) {
                    continue;
                }

                stages.add(
                    QuestStageDefinition.parse(
                        element.getAsJsonObject(),
                        stageIndex++
                    )
                );
            }
        }

        // Backwards compatibility: old flat quests become one stage.
        if (stages.isEmpty()) {
            List<QuestObjectiveDefinition> objectives =
                new ArrayList<>();

            if (json.has("objectives")
                    && json.get("objectives")
                        .isJsonArray()) {
                JsonArray array =
                    json.getAsJsonArray("objectives");
                int objectiveIndex = 0;

                for (JsonElement element : array) {
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

            stages.add(
                new QuestStageDefinition(
                    "main",
                    "",
                    string(json, "current_lead", ""),
                    stringList(json, "journal_notes"),
                    objectives
                )
            );
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
            journalSection,
            completionText,
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
            timeLimitDays,
            failureGuildReputation,
            stages,
            reward
        );
    }

    public QuestStageDefinition stage(int index) {
        int safe = Math.max(
            0,
            Math.min(index, stages.size() - 1)
        );
        return stages.get(safe);
    }

    private static String normalizeJournalSection(
        String value,
        String category
    ) {
        String normalized = normalize(value);

        if ("rumour".equals(normalized)
                || "rumor".equals(normalized)) {
            return "rumour";
        }

        if ("journal".equals(normalized)) {
            return "journal";
        }

        return "rumour".equalsIgnoreCase(category)
                || "rumor".equalsIgnoreCase(category)
            ? "rumour"
            : "journal";
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
