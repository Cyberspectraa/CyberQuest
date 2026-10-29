package com.cyberspectraa.cyberquest.quest;

import com.cyberspectraa.cyberquest.player.PlayerQuestData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Newcomer story guidance powered by the authored NPC dialogue.
 * Heard topics are recorded for each player so they count regardless of the
 * order chosen, including when players discuss later chapters early.
 */
public final class TownIntroductionQuests {
    private static final String TOPICS_KEY = "TownIntroductionHeard";
    private static final ResourceLocation[] ORDER = {
            new ResourceLocation("cyberquest", "town_getting_bearings"),
            new ResourceLocation("cyberquest", "island_whispers"),
            new ResourceLocation("cyberquest", "guild_orientation")
    };
    private static final String[][] CHAPTER_TOPICS = {
            {"pope_begin", "pope_trade"},
            {"pope_curse", "pope_north"},
            {"guild_info"}
    };

    private TownIntroductionQuests() {}

    public static void update(ServerPlayer player) {
        if (!ready(player)) return;

        CompoundTag questRoot = player.getPersistentData().getCompound(PlayerQuestData.ROOT_KEY);
        CompoundTag heard = questRoot.getCompound(TOPICS_KEY);

        for (int i = 0; i < ORDER.length; i++) {
            ResourceLocation id = ORDER[i];
            if (PlayerQuestData.isCompleted(player, id)) continue;
            if (QuestRegistry.get(id).isEmpty()) return;

            if (!PlayerQuestData.isActive(player, id)) {
                QuestManager.start(player, id, false);
            }
            if (!PlayerQuestData.isActive(player, id)) return;

            // Migrate a pre-update two-stage journal safely. Reaching stage 2
            // already proves the first conversation happened; save that fact
            // before resetting the obsolete stage counter to its single stage.
            if (PlayerQuestData.stageIndex(player, id) > 0) {
                heard.putBoolean(CHAPTER_TOPICS[i][0], true);
                questRoot.put(TOPICS_KEY, heard);
                player.getPersistentData().put(PlayerQuestData.ROOT_KEY, questRoot);
                PlayerQuestData.advanceToStage(player, id, 0);
            }

            // Already-discussed subjects still count when this chapter becomes
            // active. Only replay missing objectives to avoid per-tick writes.
            for (String topic : CHAPTER_TOPICS[i]) {
                if (heard.getBoolean(topic)
                        && PlayerQuestData.isActive(player, id)
                        && PlayerQuestData.progress(player, id, "hear_" + topic) == 0) {
                    QuestManager.recordDiscovery(player, "cybernpc:" + topic);
                }
            }
            return; // One active onboarding chapter at a time.
        }
    }

    private static boolean ready(ServerPlayer player) {
        return player != null
                && player.getPersistentData().getCompound("CyberIntro").getBoolean("Completed")
                && player.getPersistentData().getCompound("CyberRaces")
                        .getBoolean("CharacterCreated");
    }

    /** Called only after CyberNpc validates a real conversation choice. */
    public static void recordDialogue(ServerPlayer player, String action) {
        if (!ready(player) || action == null) return;

        boolean allowed = false;
        for (String[] chapter : CHAPTER_TOPICS) {
            for (String topic : chapter) {
                if (topic.equals(action)) {
                    allowed = true;
                    break;
                }
            }
            if (allowed) break;
        }
        if (!allowed) return;

        // Ensure the current quest exists before awarding its objective.
        update(player);
        CompoundTag root = player.getPersistentData().getCompound(PlayerQuestData.ROOT_KEY);
        CompoundTag heard = root.getCompound(TOPICS_KEY);
        if (!heard.getBoolean(action)) {
            heard.putBoolean(action, true);
            root.put(TOPICS_KEY, heard);
            player.getPersistentData().put(PlayerQuestData.ROOT_KEY, root);
        }

        QuestManager.recordDiscovery(player, "cybernpc:" + action);
        // Starting the next chapter also replays earlier conversations.
        update(player);
    }
}
