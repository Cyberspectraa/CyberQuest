package com.cyberspectraa.cyberquest.quest;

import com.cyberspectraa.cyberquest.player.PlayerQuestData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/** Server-driven, optional story chapters that follow the Pope's introduction. */
public final class TownIntroductionQuests {
    private static final ResourceLocation[] ORDER = {
            new ResourceLocation("cyberquest", "town_getting_bearings"),
            new ResourceLocation("cyberquest", "island_whispers"),
            new ResourceLocation("cyberquest", "guild_orientation")
    };

    private TownIntroductionQuests() {}

    public static void update(ServerPlayer player) {
        if (!player.getPersistentData().getCompound("CyberIntro").getBoolean("Completed")
                || !player.getPersistentData().getCompound("CyberRaces")
                        .getBoolean("CharacterCreated")) return;

        // Respect existing Chronicle records and never restart completed chapters.
        for (ResourceLocation id : ORDER) {
            if (PlayerQuestData.isCompleted(player, id)) continue;
            if (QuestRegistry.get(id).isEmpty()) return;
            if (!PlayerQuestData.isActive(player, id)) {
                QuestManager.start(player, id, false);
            }
            return;
        }
    }

    /** Only invoked by CyberNpc after server-side dialogue option validation. */
    public static void recordDialogue(ServerPlayer player, String action) {
        if (player == null || action == null
                || !player.getPersistentData().getCompound("CyberIntro")
                        .getBoolean("Completed")) return;

        switch (action) {
            case "pope_begin", "pope_trade", "pope_curse",
                    "pope_north", "guild_info" -> {
                QuestManager.recordDiscovery(player, "cybernpc:" + action);
                update(player);
            }
            default -> {
                // Opening a UI or unrelated dialogue never advances a chapter.
            }
        }
    }
}
