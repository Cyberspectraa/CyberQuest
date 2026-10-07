package com.cyberspectraa.cyberquest.quest;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public final class QuestFeedback {
    private QuestFeedback() {
    }

    public static void started(
        ServerPlayer player,
        QuestDefinition quest
    ) {
        player.displayClientMessage(
            Component.literal("New journal entry: ")
                .withStyle(ChatFormatting.GOLD)
                .append(
                    Component.literal(quest.title())
                        .withStyle(ChatFormatting.YELLOW)
                ),
            true
        );

        pageTurn(player, 0.95F);
    }

    public static void stageUpdated(
        ServerPlayer player,
        QuestDefinition quest,
        QuestStageDefinition stage
    ) {
        String label = stage.title().isBlank()
            ? quest.title()
            : stage.title();

        player.displayClientMessage(
            Component.literal("Journal updated: ")
                .withStyle(ChatFormatting.GOLD)
                .append(
                    Component.literal(label)
                        .withStyle(ChatFormatting.YELLOW)
                ),
            true
        );

        pageTurn(player, 1.08F);
    }

    public static void ready(
        ServerPlayer player,
        QuestDefinition quest
    ) {
        player.displayClientMessage(
            Component.literal(quest.title() + " — ")
                .withStyle(ChatFormatting.YELLOW)
                .append(
                    Component.literal(
                        "the thread is ready to resolve."
                    ).withStyle(ChatFormatting.GRAY)
                ),
            true
        );

        pageTurn(player, 1.12F);
    }

    public static void failed(
        ServerPlayer player,
        QuestDefinition quest,
        int reputationPenalty
    ) {
        String suffix = reputationPenalty > 0
            ? " Guild reputation -" + reputationPenalty
            : "";

        player.displayClientMessage(
            Component.literal(
                "Contract failed: "
            ).withStyle(ChatFormatting.RED)
            .append(
                Component.literal(quest.title())
                    .withStyle(ChatFormatting.GRAY)
            )
            .append(
                Component.literal(suffix)
                    .withStyle(ChatFormatting.DARK_RED)
            ),
            true
        );

        player.playNotifySound(
            SoundEvents.VILLAGER_NO,
            SoundSource.PLAYERS,
            0.55F,
            0.85F
        );
    }

    public static void completed(
        ServerPlayer player,
        QuestDefinition quest
    ) {
        player.displayClientMessage(
            Component.literal("Thread resolved: ")
                .withStyle(ChatFormatting.GOLD)
                .append(
                    Component.literal(quest.title())
                        .withStyle(ChatFormatting.YELLOW)
                ),
            true
        );

        player.playNotifySound(
            SoundEvents.PLAYER_LEVELUP,
            SoundSource.PLAYERS,
            0.55F,
            1.25F
        );
    }

    private static void pageTurn(
        ServerPlayer player,
        float pitch
    ) {
        player.playNotifySound(
            SoundEvents.BOOK_PAGE_TURN,
            SoundSource.PLAYERS,
            0.7F,
            pitch
        );
    }
}
