package com.cyberspectraa.cyberquest.quest;

import com.cyberspectraa.cyberquest.compat.CyberClassQuestCompat;
import com.cyberspectraa.cyberquest.compat.CyberIdentityCompat;
import com.cyberspectraa.cyberquest.compat.CyberProgressionCompat;
import com.cyberspectraa.cyberquest.compat.CyberServerWorldStateCompat;
import com.cyberspectraa.cyberquest.network.QuestNetwork;
import com.cyberspectraa.cyberquest.player.PlayerQuestData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public final class QuestManager {
    private QuestManager() {
    }

    public static boolean canStart(
        ServerPlayer player,
        QuestDefinition quest
    ) {
        if (player == null || quest == null) {
            return false;
        }

        if (PlayerQuestData.isActive(
                player,
                quest.id()
        )) {
            return false;
        }

        if (!quest.repeatable()
                && PlayerQuestData.isCompleted(
                    player,
                    quest.id()
                )) {
            return false;
        }

        for (ResourceLocation prerequisite
                : quest.prerequisites()) {
            if (!PlayerQuestData.isCompleted(
                    player,
                    prerequisite
            )) {
                return false;
            }
        }

        if (CyberProgressionCompat.getLevel(player)
                < quest.minCyberLevel()) {
            return false;
        }

        if (CyberServerWorldStateCompat.storyAct(
                player.getServer()
            ) < quest.minStoryAct()) {
            return false;
        }

        for (String flag
                : quest.requiredWorldFlags()) {
            if (!CyberServerWorldStateCompat.flag(
                    player.getServer(),
                    flag
            )) {
                return false;
            }
        }

        for (String region
                : quest.requiredUnlockedRegions()) {
            if (!CyberServerWorldStateCompat
                    .regionUnlocked(
                        player.getServer(),
                        region
                    )) {
                return false;
            }
        }

        if (!matches(
                quest.requiredRace(),
                CyberIdentityCompat.race(player)
        )) {
            return false;
        }

        if (!matches(
                quest.requiredEvolution(),
                CyberIdentityCompat.evolution(player)
        )) {
            return false;
        }

        if (!matches(
                quest.requiredClass(),
                CyberIdentityCompat.cyberClass(player)
        )) {
            return false;
        }

        return matches(
            quest.requiredClassAdvancement(),
            CyberIdentityCompat.classAdvancement(player)
        );
    }

    public static boolean start(
        ServerPlayer player,
        ResourceLocation questId,
        boolean force
    ) {
        QuestDefinition quest =
            QuestRegistry.get(
                questId
            ).orElse(null);

        if (quest == null) {
            return false;
        }

        return startDefinition(
            player,
            quest,
            force,
            null
        );
    }

    public static boolean startGenerated(
        ServerPlayer player,
        QuestDefinition quest,
        CompoundTag generatedData
    ) {
        return startDefinition(
            player,
            quest,
            false,
            generatedData
        );
    }

    private static boolean startDefinition(
        ServerPlayer player,
        QuestDefinition quest,
        boolean force,
        CompoundTag generatedData
    ) {
        ResourceLocation questId =
            quest.id();

        if (PlayerQuestData.isActive(
                player,
                questId
        )) {
            return false;
        }

        if (!force
                && !canStart(
                    player,
                    quest
                )) {
            return false;
        }

        if (force
                && !quest.repeatable()
                && PlayerQuestData.isCompleted(
                    player,
                    questId
                )) {
            PlayerQuestData.clearCompleted(
                player,
                questId
            );
        }

        if (generatedData == null) {
            PlayerQuestData.start(
                player,
                quest
            );
        } else {
            PlayerQuestData.startGenerated(
                player,
                quest,
                generatedData
            );
        }

        refreshDynamicObjectives(player);
        QuestFeedback.started(
            player,
            quest
        );
        QuestNetwork.syncJournal(
            player,
            false
        );
        return true;
    }

    public static boolean isReadyToTurnIn(
        ServerPlayer player,
        QuestDefinition quest
    ) {
        if (quest == null
                || !PlayerQuestData.isActive(
                    player,
                    quest.id()
                )) {
            return false;
        }

        int stageIndex =
            PlayerQuestData.stageIndex(
                player,
                quest.id()
            );

        if (stageIndex
                < quest.stages().size() - 1) {
            return false;
        }

        return currentStageComplete(
            player,
            quest
        );
    }

    public static boolean turnIn(
        ServerPlayer player,
        ResourceLocation questId,
        boolean force
    ) {
        QuestDefinition quest =
            QuestResolver.get(
                player,
                questId
            ).orElse(null);

        if (quest == null
                || !PlayerQuestData.isActive(
                    player,
                    questId
                )) {
            return false;
        }

        if (!force
                && !isReadyToTurnIn(
                    player,
                    quest
                )) {
            return false;
        }

        PlayerQuestData.removeActive(
            player,
            questId
        );
        PlayerQuestData.markCompleted(
            player,
            questId
        );

        grantRewards(
            player,
            quest
        );
        QuestFeedback.completed(
            player,
            quest
        );
        QuestNetwork.syncJournal(
            player,
            false
        );
        return true;
    }

    public static int turnInReadyCategory(
        ServerPlayer player,
        String category
    ) {
        int completed = 0;

        for (ResourceLocation id
                : new ArrayList<>(
                    PlayerQuestData.activeIds(
                        player
                    )
                )) {
            QuestDefinition quest =
                QuestResolver.get(
                    player,
                    id
                ).orElse(null);

            if (quest == null
                    || !quest.category()
                        .equalsIgnoreCase(
                            category
                        )
                    || !isReadyToTurnIn(
                        player,
                        quest
                    )) {
                continue;
            }

            if (turnIn(
                    player,
                    id,
                    false
            )) {
                completed++;
            }
        }

        return completed;
    }

    public static int activeCategoryCount(
        ServerPlayer player,
        String category
    ) {
        int count = 0;

        for (ResourceLocation id
                : PlayerQuestData.activeIds(
                    player
                )) {
            QuestDefinition quest =
                QuestResolver.get(
                    player,
                    id
                ).orElse(null);

            if (quest != null
                    && quest.category()
                        .equalsIgnoreCase(
                            category
                        )) {
                count++;
            }
        }

        return count;
    }

    public static void checkExpired(
        ServerPlayer player
    ) {
        boolean changed = false;

        for (ResourceLocation id
                : new ArrayList<>(
                    PlayerQuestData.activeIds(
                        player
                    )
                )) {
            long remaining =
                PlayerQuestData.timeRemaining(
                    player,
                    id
                );

            if (remaining != 0L) {
                continue;
            }

            QuestDefinition quest =
                QuestResolver.get(
                    player,
                    id
                ).orElse(null);

            if (quest == null) {
                PlayerQuestData.removeActive(
                    player,
                    id
                );
                changed = true;
                continue;
            }

            int penalty =
                PlayerQuestData
                    .failureGuildReputation(
                        player,
                        id
                    );

            PlayerQuestData.removeActive(
                player,
                id
            );

            if (penalty > 0) {
                PlayerQuestData
                    .addGuildReputation(
                        player,
                        -penalty
                    );
            }

            QuestFeedback.failed(
                player,
                quest,
                penalty
            );
            changed = true;
        }

        if (changed) {
            QuestNetwork.syncJournal(
                player,
                false
            );
        }
    }

    public static void recordKill(
        ServerPlayer player,
        Entity victim
    ) {
        ResourceLocation entityId =
            ForgeRegistries.ENTITY_TYPES
                .getKey(victim.getType());

        if (entityId != null) {
            progressMatching(
                player,
                QuestObjectiveType.KILL,
                entityId.toString(),
                1
            );
        }
    }

    public static void recordTalk(
        ServerPlayer player,
        String npcId
    ) {
        if (npcId == null
                || npcId.isBlank()) {
            return;
        }

        progressMatching(
            player,
            QuestObjectiveType.TALK,
            npcId.trim().toLowerCase(),
            1
        );
    }

    public static void recordDiscovery(
        ServerPlayer player,
        String discoveryId
    ) {
        if (discoveryId == null
                || discoveryId.isBlank()) {
            return;
        }

        progressMatching(
            player,
            QuestObjectiveType.DISCOVER,
            discoveryId.trim().toLowerCase(),
            1
        );
    }

    public static boolean tryDeliverItems(
        ServerPlayer player,
        String npcId
    ) {
        if (player == null
                || npcId == null
                || npcId.isBlank()) {
            return false;
        }

        ItemStack held =
            player.getMainHandItem();

        if (held.isEmpty()) {
            return false;
        }

        ResourceLocation heldId =
            ForgeRegistries.ITEMS
                .getKey(held.getItem());

        if (heldId == null) {
            return false;
        }

        boolean changed = false;
        String normalizedNpc =
            npcId.trim().toLowerCase();

        for (ResourceLocation questId
                : new ArrayList<>(
                    PlayerQuestData.activeIds(
                        player
                    )
                )) {
            QuestDefinition quest =
                QuestResolver.get(
                    player,
                    questId
                ).orElse(null);

            if (quest == null) {
                continue;
            }

            boolean questChanged = false;

            for (QuestObjectiveDefinition objective
                    : currentObjectives(
                        player,
                        quest
                    )) {
                if (objective.type()
                        != QuestObjectiveType.DELIVER_ITEM
                        || !npcMatches(
                            objective.npc(),
                            normalizedNpc
                        )
                        || !targetMatches(
                            objective.target(),
                            heldId.toString()
                        )) {
                    continue;
                }

                int current =
                    PlayerQuestData.progress(
                        player,
                        questId,
                        objective.id()
                    );

                int remaining = Math.max(
                    0,
                    objective.count()
                        - current
                );

                if (remaining <= 0) {
                    continue;
                }

                int delivered =
                    Math.min(
                        remaining,
                        held.getCount()
                    );

                if (delivered <= 0) {
                    continue;
                }

                if (objective.consume()
                        && !player
                            .getAbilities()
                            .instabuild) {
                    held.shrink(delivered);
                }

                questChanged |=
                    PlayerQuestData.addProgress(
                        player,
                        questId,
                        objective.id(),
                        delivered,
                        objective.count()
                    );

                if (held.isEmpty()) {
                    break;
                }
            }

            if (questChanged) {
                changed = true;
                boolean advanced =
                    advanceStagesIfReady(
                        player,
                        quest
                    );

                if (!advanced
                        && isReadyToTurnIn(
                            player,
                            quest
                        )
                        && !quest.autoTurnIn()) {
                    QuestFeedback.ready(
                        player,
                        quest
                    );
                }
            }

            if (quest.autoTurnIn()
                    && isReadyToTurnIn(
                        player,
                        quest
                    )) {
                turnIn(
                    player,
                    questId,
                    false
                );
                changed = true;
            }
        }

        if (changed) {
            QuestNetwork.syncJournal(
                player,
                false
            );
        }

        return changed;
    }

    public static void recordAdvancement(
        ServerPlayer player,
        ResourceLocation advancementId
    ) {
        if (advancementId != null) {
            progressMatching(
                player,
                QuestObjectiveType.ADVANCEMENT,
                advancementId.toString(),
                1
            );
        }
    }

    public static void refreshDynamicObjectives(
        ServerPlayer player
    ) {
        boolean changed = false;

        for (ResourceLocation questId
                : new ArrayList<>(
                    PlayerQuestData.activeIds(
                        player
                    )
                )) {
            QuestDefinition quest =
                QuestResolver.get(
                    player,
                    questId
                ).orElse(null);

            if (quest == null) {
                continue;
            }

            int guard = 0;
            boolean repeat;

            do {
                repeat = false;

                for (QuestObjectiveDefinition objective
                        : currentObjectives(
                            player,
                            quest
                        )) {
                    int value = switch (
                        objective.type()
                    ) {
                        case COLLECT ->
                            countInventory(
                                player,
                                objective.target()
                            );
                        case VISIT ->
                            isAtVisitLocation(
                                player,
                                objective
                            )
                                ? objective.count()
                                : 0;
                        default ->
                            PlayerQuestData.progress(
                                player,
                                questId,
                                objective.id()
                            );
                    };

                    if (objective.type()
                            == QuestObjectiveType.COLLECT
                            || objective.type()
                                == QuestObjectiveType.VISIT) {
                        boolean objectiveChanged =
                            PlayerQuestData.setProgress(
                                player,
                                questId,
                                objective.id(),
                                Math.min(
                                    objective.count(),
                                    value
                                )
                            );

                        changed |= objectiveChanged;
                        repeat |= objectiveChanged;
                    }
                }

                boolean advanced =
                    advanceStagesIfReady(
                        player,
                        quest
                    );

                changed |= advanced;
                repeat |= advanced;
                guard++;
            } while (repeat
                    && guard
                        < quest.stages().size()
                            + 1);

            if (quest.autoTurnIn()
                    && isReadyToTurnIn(
                        player,
                        quest
                    )) {
                turnIn(
                    player,
                    questId,
                    false
                );
                changed = true;
            }
        }

        if (changed) {
            QuestNetwork.syncJournal(
                player,
                false
            );
        }
    }

    public static List<String> progressLines(
        ServerPlayer player,
        QuestDefinition quest
    ) {
        List<String> result =
            new ArrayList<>();

        for (QuestObjectiveDefinition objective
                : currentObjectives(
                    player,
                    quest
                )) {
            int progress = Math.min(
                objective.count(),
                PlayerQuestData.progress(
                    player,
                    quest.id(),
                    objective.id()
                )
            );

            result.add(
                objective.displayText()
                    + " ["
                    + progress
                    + "/"
                    + objective.count()
                    + "]"
            );
        }

        return result;
    }

    public static QuestStageDefinition currentStage(
        ServerPlayer player,
        QuestDefinition quest
    ) {
        return quest.stage(
            PlayerQuestData.stageIndex(
                player,
                quest.id()
            )
        );
    }

    public static String currentLead(
        ServerPlayer player,
        QuestDefinition quest
    ) {
        QuestStageDefinition stage =
            currentStage(
                player,
                quest
            );

        if (!stage.lead().isBlank()) {
            return stage.lead();
        }

        for (QuestObjectiveDefinition objective
                : stage.objectives()) {
            if (PlayerQuestData.progress(
                    player,
                    quest.id(),
                    objective.id()
                ) < objective.count()) {
                return objective.displayText();
            }
        }

        return isReadyToTurnIn(
                player,
                quest
            )
            ? "Return to the quest giver to resolve this thread."
            : "Continue following the trail.";
    }

    public static List<String> journalNotes(
        ServerPlayer player,
        QuestDefinition quest
    ) {
        return currentStage(
            player,
            quest
        ).notes();
    }

    public static String stageTitle(
        ServerPlayer player,
        QuestDefinition quest
    ) {
        return currentStage(
            player,
            quest
        ).title();
    }

    public static int stageNumber(
        ServerPlayer player,
        QuestDefinition quest
    ) {
        return Math.min(
            quest.stages().size(),
            PlayerQuestData.stageIndex(
                player,
                quest.id()
            ) + 1
        );
    }

    public static void resetQuest(
        ServerPlayer player,
        ResourceLocation questId
    ) {
        PlayerQuestData.removeActive(
            player,
            questId
        );
        PlayerQuestData.clearCompleted(
            player,
            questId
        );
        QuestNetwork.syncJournal(
            player,
            false
        );
    }

    private static List<QuestObjectiveDefinition>
        currentObjectives(
            ServerPlayer player,
            QuestDefinition quest
        ) {
        return currentStage(
            player,
            quest
        ).objectives();
    }

    private static boolean currentStageComplete(
        ServerPlayer player,
        QuestDefinition quest
    ) {
        for (QuestObjectiveDefinition objective
                : currentObjectives(
                    player,
                    quest
                )) {
            if (PlayerQuestData.progress(
                    player,
                    quest.id(),
                    objective.id()
                ) < objective.count()) {
                return false;
            }
        }

        return true;
    }

    private static boolean advanceStagesIfReady(
        ServerPlayer player,
        QuestDefinition quest
    ) {
        boolean changed = false;

        while (PlayerQuestData.stageIndex(
                    player,
                    quest.id()
                ) < quest.stages().size() - 1
                && currentStageComplete(
                    player,
                    quest
                )) {
            int next =
                PlayerQuestData.stageIndex(
                    player,
                    quest.id()
                ) + 1;

            if (!PlayerQuestData.advanceToStage(
                    player,
                    quest.id(),
                    next
            )) {
                break;
            }

            changed = true;

            QuestFeedback.stageUpdated(
                player,
                quest,
                currentStage(
                    player,
                    quest
                )
            );
        }

        return changed;
    }

    private static void progressMatching(
        ServerPlayer player,
        QuestObjectiveType type,
        String target,
        int amount
    ) {
        boolean changed = false;

        for (ResourceLocation questId
                : new ArrayList<>(
                    PlayerQuestData.activeIds(
                        player
                    )
                )) {
            QuestDefinition quest =
                QuestResolver.get(
                    player,
                    questId
                ).orElse(null);

            if (quest == null) {
                continue;
            }

            boolean questChanged = false;

            for (QuestObjectiveDefinition objective
                    : currentObjectives(
                        player,
                        quest
                    )) {
                if (objective.type() != type
                        || !targetMatches(
                            objective.target(),
                            target
                        )) {
                    continue;
                }

                questChanged |=
                    PlayerQuestData.addProgress(
                        player,
                        questId,
                        objective.id(),
                        amount,
                        objective.count()
                    );
            }

            if (questChanged) {
                changed = true;
                boolean advanced =
                    advanceStagesIfReady(
                        player,
                        quest
                    );

                if (!advanced
                        && isReadyToTurnIn(
                            player,
                            quest
                        )
                        && !quest.autoTurnIn()) {
                    QuestFeedback.ready(
                        player,
                        quest
                    );
                }
            }

            if (quest.autoTurnIn()
                    && isReadyToTurnIn(
                        player,
                        quest
                    )) {
                turnIn(
                    player,
                    questId,
                    false
                );
                changed = true;
            }
        }

        if (changed) {
            QuestNetwork.syncJournal(
                player,
                false
            );
        }
    }

    private static boolean targetMatches(
        String expected,
        String actual
    ) {
        return expected == null
            || expected.isBlank()
            || "*".equals(expected)
            || expected.equalsIgnoreCase(actual);
    }

    private static boolean npcMatches(
        String expected,
        String actual
    ) {
        return expected == null
            || expected.isBlank()
            || "*".equals(expected)
            || expected.equalsIgnoreCase(actual);
    }

    private static boolean matches(
        String required,
        String actual
    ) {
        return required == null
            || required.isBlank()
            || required.equalsIgnoreCase(actual);
    }

    private static int countInventory(
        ServerPlayer player,
        String target
    ) {
        if (target == null
                || target.isBlank()) {
            return 0;
        }

        ResourceLocation itemId =
            ResourceLocation.tryParse(target);

        if (itemId == null) {
            return 0;
        }

        int count = 0;

        for (int slot = 0;
                slot < player.getInventory()
                    .getContainerSize();
                slot++) {
            ItemStack stack =
                player.getInventory()
                    .getItem(slot);

            if (stack.isEmpty()) {
                continue;
            }

            ResourceLocation stackId =
                ForgeRegistries.ITEMS
                    .getKey(stack.getItem());

            if (itemId.equals(stackId)) {
                count += stack.getCount();
            }
        }

        return count;
    }

    private static boolean isAtVisitLocation(
        ServerPlayer player,
        QuestObjectiveDefinition objective
    ) {
        if (!objective.dimension().isBlank()
                && !objective.dimension().equals(
                    player.level()
                        .dimension()
                        .location()
                        .toString()
                )) {
            return false;
        }

        double dx =
            player.getX() - objective.x();
        double dy =
            player.getY() - objective.y();
        double dz =
            player.getZ() - objective.z();
        double radius = objective.radius();

        return dx * dx
            + dy * dy
            + dz * dz
            <= radius * radius;
    }

    private static void grantRewards(
        ServerPlayer player,
        QuestDefinition quest
    ) {
        QuestReward reward =
            quest.reward();

        double multiplier =
            CyberServerWorldStateCompat
                .rewardMultiplier(
                    player.getServer()
                );

        int vanillaXp = (int) Math.min(
            Integer.MAX_VALUE,
            Math.max(
                0L,
                Math.round(
                    reward.vanillaXp()
                        * multiplier
                )
            )
        );

        long cyberXp = Math.max(
            0L,
            Math.round(
                reward.cyberXp()
                    * multiplier
            )
        );

        if (vanillaXp > 0) {
            player.giveExperiencePoints(
                vanillaXp
            );
        }

        if (cyberXp > 0L) {
            CyberProgressionCompat
                .addExperience(
                    player,
                    cyberXp
                );
        }

        if (reward.guildReputation() != 0) {
            PlayerQuestData
                .addGuildReputation(
                    player,
                    reward.guildReputation()
                );
        }

        if (!reward.classUnlock().isBlank()) {
            CyberClassQuestCompat.grantUnlock(
                player,
                quest.requiredClass(),
                reward.classUnlock()
            );
        }

        for (QuestReward.ItemReward itemReward
                : reward.items()) {
            Item item =
                ForgeRegistries.ITEMS
                    .getValue(
                        itemReward.itemId()
                    );

            if (item == null) {
                continue;
            }

            int remaining =
                itemReward.count();
            int maxStack = Math.max(
                1,
                new ItemStack(item)
                    .getMaxStackSize()
            );

            while (remaining > 0) {
                int amount = Math.min(
                    remaining,
                    maxStack
                );

                ItemStack stack =
                    new ItemStack(
                        item,
                        amount
                    );

                if (!player.getInventory()
                    .add(stack)) {
                    player.drop(
                        stack,
                        false
                    );
                }

                remaining -= amount;
            }
        }
    }
}
