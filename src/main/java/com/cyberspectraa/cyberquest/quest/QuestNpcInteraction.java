package com.cyberspectraa.cyberquest.quest;

import com.cyberspectraa.cyberquest.compat.CyberNpcQuestCompat;
import com.cyberspectraa.cyberquest.compat.CyberServerWorldStateCompat;
import com.cyberspectraa.cyberquest.player.PlayerQuestData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class QuestNpcInteraction {
    private QuestNpcInteraction() {
    }

    public static boolean handle(
        ServerPlayer player,
        Entity npc
    ) {
        if (!CyberNpcQuestCompat.isCyberNpc(npc)) {
            return false;
        }

        String npcId = CyberNpcQuestCompat.npcId(npc);

        if (!npcId.isBlank()) {
            QuestManager.recordTalk(player, npcId);
        }

        Set<ResourceLocation> mergedBindings =
            new LinkedHashSet<>(
                CyberNpcQuestCompat.bindings(npc)
            );

        if (!npcId.isBlank()) {
            for (String value
                    : CyberServerWorldStateCompat
                        .questBindings(
                            player.getServer(),
                            npcId
                        )) {
                ResourceLocation id =
                    ResourceLocation.tryParse(value);

                if (id != null) {
                    mergedBindings.add(id);
                }
            }
        }

        List<ResourceLocation> bindings =
            new ArrayList<>(mergedBindings);

        for (ResourceLocation questId : bindings) {
            QuestDefinition quest =
                QuestRegistry.get(questId).orElse(null);

            if (quest == null) {
                continue;
            }

            if (PlayerQuestData.isActive(
                    player,
                    questId
                )) {
                QuestManager.tryDeliverItems(
                    player,
                    npcId
                );
                QuestManager.refreshDynamicObjectives(player);

                if (QuestManager.isReadyToTurnIn(
                        player,
                        quest
                    )) {
                    return QuestManager.turnIn(
                        player,
                        questId,
                        false
                    );
                }

                player.sendSystemMessage(
                    Component.literal(
                        quest.title() + " — In Progress"
                    ).withStyle(ChatFormatting.YELLOW)
                );

                for (String line
                        : QuestManager.progressLines(
                            player,
                            quest
                        )) {
                    player.sendSystemMessage(
                        Component.literal("• " + line)
                            .withStyle(
                                ChatFormatting.GRAY
                            )
                    );
                }

                return true;
            }

            if (QuestManager.canStart(player, quest)) {
                return QuestManager.start(
                    player,
                    questId,
                    false
                );
            }
        }

        if (!bindings.isEmpty()) {
            player.sendSystemMessage(
                Component.literal(
                    "This NPC has no quest available for you right now."
                ).withStyle(ChatFormatting.GRAY)
            );
            return true;
        }

        if (CyberNpcQuestCompat.isQuestNpc(npc)) {
            player.sendSystemMessage(
                Component.literal(
                    "This Quest NPC has no CyberQuest quest bound to it yet."
                ).withStyle(ChatFormatting.GRAY)
            );
            return true;
        }

        return !npcId.isBlank();
    }
}
