package com.cyberspectraa.cyberquest.quest;

import com.cyberspectraa.cyberquest.CyberQuest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class QuestRegistry
    extends SimpleJsonResourceReloadListener {

    private static final Gson GSON =
        new GsonBuilder().setPrettyPrinting().create();

    private static volatile Map<ResourceLocation, QuestDefinition>
        quests = Map.of();

    public QuestRegistry() {
        super(GSON, "cyberquests");
    }

    @Override
    protected void apply(
        Map<ResourceLocation, JsonElement> resources,
        ResourceManager resourceManager,
        ProfilerFiller profiler
    ) {
        Map<ResourceLocation, QuestDefinition> loaded =
            new LinkedHashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry
                : resources.entrySet()) {
            try {
                if (!entry.getValue().isJsonObject()) {
                    CyberQuest.LOGGER.warn(
                        "Ignoring quest {} because its JSON root is not an object",
                        entry.getKey()
                    );
                    continue;
                }

                QuestDefinition quest = QuestDefinition.parse(
                    entry.getKey(),
                    entry.getValue().getAsJsonObject()
                );

                boolean hasObjectives =
                    quest.stages().stream()
                        .anyMatch(stage ->
                            !stage.objectives().isEmpty()
                        );

                if (!hasObjectives) {
                    CyberQuest.LOGGER.warn(
                        "Quest {} has no stage objectives",
                        entry.getKey()
                    );
                }

                loaded.put(entry.getKey(), quest);
            } catch (RuntimeException exception) {
                CyberQuest.LOGGER.error(
                    "Failed to load quest {}",
                    entry.getKey(),
                    exception
                );
            }
        }

        quests = Collections.unmodifiableMap(loaded);
        CyberQuest.LOGGER.info(
            "Loaded {} CyberQuest definitions",
            quests.size()
        );
    }

    public static Optional<QuestDefinition> get(
        ResourceLocation id
    ) {
        return Optional.ofNullable(quests.get(id));
    }

    public static Collection<QuestDefinition> all() {
        return quests.values();
    }

    public static Map<ResourceLocation, QuestDefinition> snapshot() {
        return quests;
    }
}
