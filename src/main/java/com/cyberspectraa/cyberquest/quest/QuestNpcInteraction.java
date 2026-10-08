package com.cyberspectraa.cyberquest.quest;

import com.cyberspectraa.cyberquest.compat.CyberNpcQuestCompat;
import com.cyberspectraa.cyberquest.compat.CyberServerWorldStateCompat;
import com.cyberspectraa.cyberquest.guild.GuildContractManager;
import com.cyberspectraa.cyberquest.player.PlayerQuestData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Bridges CyberNpc interaction into the quest system without owning or
 * cancelling the NPC's normal interaction. Quest state may update alongside
 * dialogue, trading, or other CyberNpc behaviour.
 */
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

        if (CyberNpcQuestCompat.isGuildReceptionist(npc)) {
            GuildContractManager.receptionistInteraction(player);
            return true;
        }

        String npcId =
            CyberNpcQuestCompat.npcId(npc);

        if (!npcId.isBlank()) {
            QuestManager.recordTalk(
                player,
                npcId
            );
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

                QuestManager.refreshDynamicObjectives(
                    player
                );

                if (QuestManager.isReadyToTurnIn(
                        player,
                        quest
                    )) {
                    QuestManager.turnIn(
                        player,
                        questId,
                        false
                    );
                }

                // An active bound quest is enough for this interaction.
                // Do not block the NPC's own dialogue or other behaviour.
                return false;
            }

            if (QuestManager.canStart(
                    player,
                    quest
                )) {
                QuestManager.start(
                    player,
                    questId,
                    false
                );

                // Start at most one bound quest per interaction.
                return false;
            }
        }
        return false;
    }
}
