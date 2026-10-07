package com.cyberspectraa.cyberquest.guild;

import com.cyberspectraa.cyberquest.network.QuestNetwork;
import com.cyberspectraa.cyberquest.player.PlayerQuestData;
import com.cyberspectraa.cyberquest.quest.QuestManager;
import com.cyberspectraa.cyberquest.registry.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public final class GuildContractManager {
    public static final int MAX_ACTIVE_CONTRACTS = 3;

    private GuildContractManager() {
    }

    public static void openBoard(
        ServerPlayer player,
        BlockPos pos
    ) {
        QuestManager.turnInReadyCategory(
            player,
            "guild"
        );

        QuestNetwork.openGuildBoard(
            player,
            pos
        );
    }

    public static boolean accept(
        ServerPlayer player,
        BlockPos pos,
        int slot
    ) {
        if (!(player.level()
                instanceof ServerLevel level)
                || slot < 0
                || slot
                    >= GuildContractGenerator
                        .OFFER_COUNT
                || player.blockPosition()
                    .distSqr(pos)
                    > 64.0D
                || !level.getBlockState(pos)
                    .is(ModBlocks.GUILD_BOARD.get())) {
            return false;
        }

        if (QuestManager.activeCategoryCount(
                player,
                "guild"
        ) >= MAX_ACTIVE_CONTRACTS) {
            player.displayClientMessage(
                Component.literal(
                    "Your journal already holds three active guild contracts."
                ).withStyle(
                    ChatFormatting.YELLOW
                ),
                true
            );
            return false;
        }

        List<ProceduralQuestOffer> offers =
            GuildContractGenerator.offers(
                level,
                pos
            );
        ProceduralQuestOffer offer =
            offers.get(slot);

        long day =
            level.getDayTime() / 24000L;

        if (PlayerQuestData.isActive(
                player,
                offer.id()
        )
                || PlayerQuestData.hasGuildOfferTaken(
                    player,
                    offer.id(),
                    day
                )) {
            return false;
        }

        boolean started =
            QuestManager.startGenerated(
                player,
                offer.definition(),
                offer.toTag()
            );

        if (started) {
            PlayerQuestData.markGuildOfferTaken(
                player,
                offer.id(),
                day
            );
            QuestNetwork.openGuildBoard(
                player,
                pos
            );
        }

        return started;
    }
}
