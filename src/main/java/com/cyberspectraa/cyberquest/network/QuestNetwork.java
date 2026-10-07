package com.cyberspectraa.cyberquest.network;

import com.cyberspectraa.cyberquest.CyberQuest;
import com.cyberspectraa.cyberquest.guild.GuildContractGenerator;
import com.cyberspectraa.cyberquest.guild.ProceduralQuestOffer;
import com.cyberspectraa.cyberquest.network.packet.AcceptGuildContractPacket;
import com.cyberspectraa.cyberquest.network.packet.JournalSyncPacket;
import com.cyberspectraa.cyberquest.network.packet.OpenGuildBoardPacket;
import com.cyberspectraa.cyberquest.network.packet.RequestJournalPacket;
import com.cyberspectraa.cyberquest.network.packet.SetTrackedQuestPacket;
import com.cyberspectraa.cyberquest.player.PlayerQuestData;
import com.cyberspectraa.cyberquest.quest.QuestDefinition;
import com.cyberspectraa.cyberquest.quest.QuestManager;
import com.cyberspectraa.cyberquest.quest.QuestRegistry;
import com.cyberspectraa.cyberquest.quest.QuestResolver;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.ArrayList;
import java.util.List;

public final class QuestNetwork {
    private static final String PROTOCOL = "3";

    public static final SimpleChannel CHANNEL =
        NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CyberQuest.MOD_ID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
        );

    private static int messageId;

    private QuestNetwork() {
    }

    public static void init() {
        CHANNEL.messageBuilder(
                RequestJournalPacket.class,
                messageId++,
                NetworkDirection.PLAY_TO_SERVER
            )
            .encoder(RequestJournalPacket::encode)
            .decoder(RequestJournalPacket::decode)
            .consumerMainThread(RequestJournalPacket::handle)
            .add();

        CHANNEL.messageBuilder(
                SetTrackedQuestPacket.class,
                messageId++,
                NetworkDirection.PLAY_TO_SERVER
            )
            .encoder(SetTrackedQuestPacket::encode)
            .decoder(SetTrackedQuestPacket::decode)
            .consumerMainThread(SetTrackedQuestPacket::handle)
            .add();

        CHANNEL.messageBuilder(
                AcceptGuildContractPacket.class,
                messageId++,
                NetworkDirection.PLAY_TO_SERVER
            )
            .encoder(AcceptGuildContractPacket::encode)
            .decoder(AcceptGuildContractPacket::decode)
            .consumerMainThread(AcceptGuildContractPacket::handle)
            .add();

        CHANNEL.messageBuilder(
                JournalSyncPacket.class,
                messageId++,
                NetworkDirection.PLAY_TO_CLIENT
            )
            .encoder(JournalSyncPacket::encode)
            .decoder(JournalSyncPacket::decode)
            .consumerMainThread(JournalSyncPacket::handle)
            .add();

        CHANNEL.messageBuilder(
                OpenGuildBoardPacket.class,
                messageId++,
                NetworkDirection.PLAY_TO_CLIENT
            )
            .encoder(OpenGuildBoardPacket::encode)
            .decoder(OpenGuildBoardPacket::decode)
            .consumerMainThread(OpenGuildBoardPacket::handle)
            .add();
    }

    public static void sendToServer(Object packet) {
        CHANNEL.sendToServer(packet);
    }

    public static void syncJournal(
        ServerPlayer player,
        boolean open
    ) {
        if (player == null) {
            return;
        }

        CHANNEL.send(
            PacketDistributor.PLAYER.with(() -> player),
            new JournalSyncPacket(
                open,
                buildEntries(player)
            )
        );
    }

    public static void openGuildBoard(
        ServerPlayer player,
        BlockPos pos,
        int slot
    ) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        List<ProceduralQuestOffer> offers =
            GuildContractGenerator.offers(level, pos);

        if (slot < 0 || slot >= offers.size()) {
            return;
        }

        ProceduralQuestOffer offer =
            offers.get(slot);

        long day =
            level.getDayTime() / 24000L;

        String state =
            PlayerQuestData.isActive(player, offer.id())
                ? "ACTIVE"
                : PlayerQuestData.hasGuildOfferTaken(
                    player,
                    offer.id(),
                    day
                )
                    ? "COMPLETED"
                    : "AVAILABLE";

        String reward =
            "+" + offer.rewardGuildReputation()
                + " guild rep • "
                + offer.silverCoins()
                + " silver";

        String timer =
            offer.durationDays() <= 0
                ? ""
                : offer.durationDays()
                    + (offer.durationDays() == 1
                        ? " day"
                        : " days");

        String penalty =
            offer.failureGuildReputation() <= 0
                ? ""
                : "Fail: -"
                    + offer.failureGuildReputation()
                    + " rep";

        CHANNEL.send(
            PacketDistributor.PLAYER.with(() -> player),
            new OpenGuildBoardPacket(
                pos,
                PlayerQuestData.guildReputation(player),
                List.of(
                    new GuildBoardOfferData(
                        slot,
                        offer.id().toString(),
                        offer.title(),
                        offer.objectiveText(),
                        reward,
                        timer,
                        penalty,
                        state
                    )
                )
            )
        );
    }

    private static List<JournalEntry> buildEntries(
        ServerPlayer player
    ) {
        List<JournalEntry> result = new ArrayList<>();

        for (ResourceLocation questId
                : PlayerQuestData.activeIds(player)) {
            QuestDefinition quest =
                QuestResolver.get(player, questId).orElse(null);

            if (quest == null) {
                continue;
            }

            result.add(
                new JournalEntry(
                    questId.toString(),
                    quest.title(),
                    quest.description(),
                    quest.category(),
                    quest.journalSection(),
                    QuestManager.isReadyToTurnIn(player, quest)
                        ? "READY"
                        : "ACTIVE",
                    QuestManager.currentLead(player, quest),
                    formatTimeRemaining(
                        PlayerQuestData.timeRemaining(player, questId)
                    ),
                    QuestManager.stageTitle(player, quest),
                    QuestManager.stageNumber(player, quest),
                    quest.stages().size(),
                    "",
                    QuestManager.journalNotes(player, quest),
                    QuestManager.progressLines(player, quest),
                    PlayerQuestData.isTracked(player, questId)
                )
            );
        }

        // Daily procedural jobs are intentionally omitted from Chronicle so it
        // remains a readable history of authored story and class adventures.
        for (ResourceLocation questId
                : PlayerQuestData.completedIds(player)) {
            QuestDefinition quest =
                QuestRegistry.get(questId).orElse(null);

            if (quest == null) {
                continue;
            }

            result.add(
                new JournalEntry(
                    questId.toString(),
                    quest.title(),
                    quest.description(),
                    quest.category(),
                    "chronicle",
                    "COMPLETED",
                    "",
                    "",
                    "",
                    quest.stages().size(),
                    quest.stages().size(),
                    quest.completionText(),
                    List.of(),
                    List.of(),
                    false
                )
            );
        }

        return result;
    }

    private static String formatTimeRemaining(long ticks) {
        if (ticks < 0L) {
            return "";
        }

        if (ticks == 0L) {
            return "Expired";
        }

        long days = ticks / 24000L;
        long remainder = ticks % 24000L;
        long hours = (remainder + 999L) / 1000L;

        if (days > 0L) {
            return days
                + (days == 1L ? " day" : " days")
                + (hours > 0L ? ", " + hours + "h" : "")
                + " remaining";
        }

        return Math.max(1L, hours) + "h remaining";
    }
}
