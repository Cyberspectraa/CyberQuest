package com.cyberspectraa.cyberquest.quest;

import com.cyberspectraa.cyberquest.compat.CyberIdentityCompat;
import com.cyberspectraa.cyberquest.compat.CyberProgressionCompat;
import com.cyberspectraa.cyberquest.compat.CyberServerWorldStateCompat;
import com.cyberspectraa.cyberquest.network.QuestNetwork;
import com.cyberspectraa.cyberquest.player.PlayerQuestData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        if (PlayerQuestData.isActive(player, quest.id())) {
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

        for (String flag : quest.requiredWorldFlags()) {
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
            QuestRegistry.get(questId).orElse(null);

        if (quest == null
                || PlayerQuestData.isActive(
                    player,
                    questId
                )) {
            return false;
        }

        if (!force && !canStart(player, quest)) {
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

        PlayerQuestData.start(player, questId);
        refreshDynamicObjectives(player);

        player.sendSystemMessage(
            Component.literal("Quest Started: ")
                .withStyle(ChatFormatting.GOLD)
                .append(
                    Component.literal(quest.title())
                        .withStyle(ChatFormatting.YELLOW)
                )
        );

        QuestNetwork.syncJournal(player, false);
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

        for (QuestObjectiveDefinition objective
                : quest.objectives()) {
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

    public static boolean turnIn(
        ServerPlayer player,
        ResourceLocation questId,
        boolean force
    ) {
        QuestDefinition quest =
            QuestRegistry.get(questId).orElse(null);

        if (quest == null
                || !PlayerQuestData.isActive(
                    player,
                    questId
                )) {
            return false;
        }

        if (!force && !isReadyToTurnIn(player, quest)) {
            return false;
        }

        PlayerQuestData.removeActive(player, questId);
        PlayerQuestData.markCompleted(player, questId);

        grantRewards(player, quest.reward());

        player.sendSystemMessage(
            Component.literal("Quest Complete: ")
                .withStyle(ChatFormatting.GREEN)
                .append(
                    Component.literal(quest.title())
                        .withStyle(ChatFormatting.YELLOW)
                )
        );

        QuestNetwork.syncJournal(player, false);
        return true;
    }

    public static void recordKill(
        ServerPlayer player,
        Entity victim
    ) {
        ResourceLocation entityId =
            ForgeRegistries.ENTITY_TYPES.getKey(
                victim.getType()
            );

        if (entityId == null) {
            return;
        }

        progressMatching(
            player,
            QuestObjectiveType.KILL,
            entityId.toString(),
            1
        );
    }

    public static void recordTalk(
        ServerPlayer player,
        String npcId
    ) {
        if (npcId == null || npcId.isBlank()) {
            return;
        }

        progressMatching(
            player,
            QuestObjectiveType.TALK,
            npcId.trim().toLowerCase(),
            1
        );
    }

    public static void recordAdvancement(
        ServerPlayer player,
        ResourceLocation advancementId
    ) {
        if (advancementId == null) {
            return;
        }

        progressMatching(
            player,
            QuestObjectiveType.ADVANCEMENT,
            advancementId.toString(),
            1
        );
    }

    public static void refreshDynamicObjectives(
        ServerPlayer player
    ) {
        boolean changed = false;

        for (ResourceLocation questId
                : new ArrayList<>(
                    PlayerQuestData.activeIds(player)
                )) {
            QuestDefinition quest =
                QuestRegistry.get(questId).orElse(null);

            if (quest == null) {
                continue;
            }

            for (QuestObjectiveDefinition objective
                    : quest.objectives()) {
                int value = switch (objective.type()) {
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
                    changed |= PlayerQuestData.setProgress(
                        player,
                        questId,
                        objective.id(),
                        Math.min(
                            objective.count(),
                            value
                        )
                    );
                }
            }

            if (quest.autoTurnIn()
                    && isReadyToTurnIn(
                        player,
                        quest
                    )) {
                turnIn(player, questId, false);
                changed = true;
            }
        }

        if (changed) {
            QuestNetwork.syncJournal(player, false);
        }
    }

    public static List<String> progressLines(
        ServerPlayer player,
        QuestDefinition quest
    ) {
        List<String> result = new ArrayList<>();

        for (QuestObjectiveDefinition objective
                : quest.objectives()) {
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
                    + " [" + progress
                    + "/" + objective.count() + "]"
            );
        }

        return result;
    }

    public static void resetQuest(
        ServerPlayer player,
        ResourceLocation questId
    ) {
        PlayerQuestData.removeActive(player, questId);
        PlayerQuestData.clearCompleted(player, questId);
        QuestNetwork.syncJournal(player, false);
    }

    private static void progressMatching(
        ServerPlayer player,
        QuestObjectiveType type,
        String target,
        int amount
    ) {
        boolean changed = false;

        List<ResourceLocation> active =
            new ArrayList<>(
                PlayerQuestData.activeIds(player)
            );

        for (ResourceLocation questId : active) {
            QuestDefinition quest =
                QuestRegistry.get(questId).orElse(null);

            if (quest == null) {
                continue;
            }

            for (QuestObjectiveDefinition objective
                    : quest.objectives()) {
                if (objective.type() != type
                        || !targetMatches(
                            objective.target(),
                            target
                        )) {
                    continue;
                }

                changed |= PlayerQuestData.addProgress(
                    player,
                    questId,
                    objective.id(),
                    amount,
                    objective.count()
                );
            }

            if (quest.autoTurnIn()
                    && isReadyToTurnIn(
                        player,
                        quest
                    )) {
                turnIn(player, questId, false);
                changed = true;
            }
        }

        if (changed) {
            QuestNetwork.syncJournal(player, false);
        }
    }

    private static boolean targetMatches(
        String expected,
        String actual
    ) {
        if (expected == null
                || expected.isBlank()
                || "*".equals(expected)) {
            return true;
        }

        return expected.equalsIgnoreCase(actual);
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
        if (target == null || target.isBlank()) {
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
                player.getInventory().getItem(slot);

            if (stack.isEmpty()) {
                continue;
            }

            ResourceLocation stackId =
                ForgeRegistries.ITEMS.getKey(
                    stack.getItem()
                );

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

        double dx = player.getX() - objective.x();
        double dy = player.getY() - objective.y();
        double dz = player.getZ() - objective.z();
        double radius = objective.radius();

        return dx * dx + dy * dy + dz * dz
            <= radius * radius;
    }

    private static void grantRewards(
        ServerPlayer player,
        QuestReward reward
    ) {
        double multiplier =
            CyberServerWorldStateCompat.rewardMultiplier(
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
            player.giveExperiencePoints(vanillaXp);
        }

        if (cyberXp > 0L) {
            CyberProgressionCompat.addExperience(
                player,
                cyberXp
            );
        }

        for (QuestReward.ItemReward itemReward
                : reward.items()) {
            Item item = ForgeRegistries.ITEMS.getValue(
                itemReward.itemId()
            );

            if (item == null) {
                continue;
            }

            int remaining = itemReward.count();
            int maxStack = Math.max(
                1,
                new ItemStack(item).getMaxStackSize()
            );

            while (remaining > 0) {
                int amount = Math.min(
                    remaining,
                    maxStack
                );

                ItemStack stack =
                    new ItemStack(item, amount);

                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }

                remaining -= amount;
            }
        }
    }
}
