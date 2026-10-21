package com.cyberspectraa.cyberquest.quest;

import com.cyberspectraa.cyberquest.network.JournalEntry;
import com.cyberspectraa.cyberquest.network.QuestNetwork;
import com.cyberspectraa.cyberquest.player.PlayerQuestData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * First-story objective for island newcomers. No arbitrary walls or
 * teleportation: the journal guides players to guild class registration.
 * Uses existing CyberIntro/CyberClasses saved player state, without hard
 * dependencies on either mod or modifying their class or progression data.
 */
public final class GuildRegistrationMainQuest {
    public static final ResourceLocation ID =
            new ResourceLocation("cyberquest", "guild_registration");

    private static final String STARTED = "GuildOnboardingStarted";
    private static final String COMPLETE = "GuildOnboardingCompleted";
    private static final String UNTRACKED = "GuildOnboardingUntracked";

    private GuildRegistrationMainQuest() {}

    public static void update(ServerPlayer player) {
        CompoundTag questData = player.getPersistentData().getCompound(PlayerQuestData.ROOT_KEY);
        boolean started = questData.getBoolean(STARTED);
        boolean introFinished = player.getPersistentData().getCompound("CyberIntro")
                .getBoolean("Completed");
        boolean characterCreated = player.getPersistentData().getCompound("CyberRaces")
                .getBoolean("CharacterCreated");
        boolean classChosen = classChosen(player);

        // Do not invent this tutorial for old players who already have a
        // class. The quest starts only once the Pope has finished the intro.
        if (!started && characterCreated && introFinished && !classChosen) {
            questData.putBoolean(STARTED, true);
            player.getPersistentData().put(PlayerQuestData.ROOT_KEY, questData);
            player.sendSystemMessage(Component.literal(
                    "Main Quest: Your First Steps — register a class at the Adventurers' Guild. "
                    + "Starter supplies are optional. See your journal for details."));
            QuestNetwork.syncJournal(player, false);
            return;
        }

        // Class selection finishes the quest automatically. Never reset or
        // repeat it on death, reconnect or changes to starter equipment.
        if (started && !questData.getBoolean(COMPLETE) && classChosen) {
            questData.putBoolean(COMPLETE, true);
            player.getPersistentData().put(PlayerQuestData.ROOT_KEY, questData);
            player.sendSystemMessage(Component.literal(
                    "Main Quest Complete: Your First Steps."));
            QuestNetwork.syncJournal(player, false);
        }
    }

    public static JournalEntry entry(ServerPlayer player) {
        CompoundTag data = player.getPersistentData().getCompound(PlayerQuestData.ROOT_KEY);
        if (!data.getBoolean(STARTED)) return null;
        boolean complete = data.getBoolean(COMPLETE) || classChosen(player);
        boolean tracked = !complete && PlayerQuestData.trackedQuestId(player) == null
                && !data.getBoolean(UNTRACKED);
        return new JournalEntry(
                ID.toString(),
                "Your First Steps",
                "The Pope has advised you to visit the Adventurers' Guild. "
                    + "Register a class to begin training. You may accept or decline "
                    + "the Guild's one-time starter equipment.",
                "main",
                complete ? "chronicle" : "journal",
                complete ? "COMPLETED" : "ACTIVE",
                complete ? "" : "Speak with the Guild Receptionist and choose a class.",
                "",
                "Register at the Adventurers' Guild",
                1, 1,
                complete ? "You registered your first class. The island is yours to explore." : "",
                List.of("Talk to the Guild Receptionist.",
                        "Choose any available class; starter gear is optional."),
                complete ? List.of() : List.of("Register a class with the Guild Receptionist."),
                tracked
        );
    }

    /** Returns true when this special journal entry handled a track request. */
    public static boolean handleTracking(ServerPlayer player, ResourceLocation id) {
        JournalEntry entry = entry(player);
        if (entry == null || "COMPLETED".equals(entry.status())) return false;
        boolean selectMain = ID.equals(id);
        // Null means 'untrack' if this was the current tracked entry.
        boolean untrackMain = id == null && entry.tracked();
        if (!selectMain && !untrackMain) return false;
        CompoundTag data = player.getPersistentData().getCompound(PlayerQuestData.ROOT_KEY);
        data.putBoolean(UNTRACKED, untrackMain);
        player.getPersistentData().put(PlayerQuestData.ROOT_KEY, data);
        if (selectMain) PlayerQuestData.setTrackedQuest(player, null);
        return true;
    }

    private static boolean classChosen(ServerPlayer player) {
        return player.getPersistentData().getCompound("CyberClasses")
                .getBoolean("ClassChosen");
    }
}
