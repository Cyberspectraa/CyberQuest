package com.cyberspectraa.cyberquest.quest;

import com.cyberspectraa.cyberquest.guild.ProceduralQuestOffer;
import com.cyberspectraa.cyberquest.player.PlayerQuestData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public final class QuestResolver {
    private QuestResolver() {
    }

    public static Optional<QuestDefinition> get(
        ServerPlayer player,
        ResourceLocation id
    ) {
        QuestDefinition staticQuest =
            QuestRegistry.get(id).orElse(null);

        if (staticQuest != null) {
            return Optional.of(staticQuest);
        }

        CompoundTag generated =
            PlayerQuestData.generatedData(
                player,
                id
            );

        if (generated == null) {
            return Optional.empty();
        }

        ProceduralQuestOffer offer =
            ProceduralQuestOffer.fromTag(
                generated
            );

        if (offer == null
                || !offer.id().equals(id)) {
            return Optional.empty();
        }

        return Optional.of(
            offer.definition()
        );
    }
}
