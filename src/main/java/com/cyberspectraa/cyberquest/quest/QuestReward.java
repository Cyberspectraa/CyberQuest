package com.cyberspectraa.cyberquest.quest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record QuestReward(
    long cyberXp,
    int vanillaXp,
    List<ItemReward> items
) {
    public QuestReward {
        cyberXp = Math.max(0L, cyberXp);
        vanillaXp = Math.max(0, vanillaXp);
        items = items == null ? List.of() : List.copyOf(items);
    }

    public static QuestReward empty() {
        return new QuestReward(0L, 0, List.of());
    }

    public static QuestReward parse(JsonObject json) {
        if (json == null) {
            return empty();
        }

        long cyberXp = json.has("cyber_xp")
            ? Math.max(0L, json.get("cyber_xp").getAsLong())
            : 0L;

        int vanillaXp = json.has("vanilla_xp")
            ? Math.max(0, json.get("vanilla_xp").getAsInt())
            : 0;

        List<ItemReward> items = new ArrayList<>();

        if (json.has("items") && json.get("items").isJsonArray()) {
            JsonArray array = json.getAsJsonArray("items");

            for (JsonElement element : array) {
                if (!element.isJsonObject()) {
                    continue;
                }

                JsonObject item = element.getAsJsonObject();

                if (!item.has("item")) {
                    continue;
                }

                ResourceLocation id = ResourceLocation.tryParse(
                    item.get("item").getAsString()
                );

                if (id == null) {
                    continue;
                }

                int count = item.has("count")
                    ? Math.max(1, item.get("count").getAsInt())
                    : 1;

                items.add(new ItemReward(id, count));
            }
        }

        return new QuestReward(cyberXp, vanillaXp, items);
    }

    public record ItemReward(ResourceLocation itemId, int count) {
        public ItemReward {
            count = Math.max(1, count);
        }
    }
}
