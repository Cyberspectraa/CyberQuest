package com.cyberspectraa.cyberquest.network;

import com.cyberspectraa.cyberquest.CyberQuest;
import com.cyberspectraa.cyberquest.network.packet.JournalSyncPacket;
import com.cyberspectraa.cyberquest.network.packet.RequestJournalPacket;
import com.cyberspectraa.cyberquest.network.packet.SetTrackedQuestPacket;
import com.cyberspectraa.cyberquest.player.PlayerQuestData;
import com.cyberspectraa.cyberquest.quest.QuestDefinition;
import com.cyberspectraa.cyberquest.quest.QuestManager;
import com.cyberspectraa.cyberquest.quest.QuestRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.ArrayList;
import java.util.List;

public final class QuestNetwork {
    private static final String PROTOCOL = "2";

    public static final SimpleChannel CHANNEL =
        NetworkRegistry.newSimpleChannel(
            new ResourceLocation(
                CyberQuest.MOD_ID,
                "main"
            ),
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
                JournalSyncPacket.class,
                messageId++,
                NetworkDirection.PLAY_TO_CLIENT
            )
            .encoder(JournalSyncPacket::encode)
            .decoder(JournalSyncPacket::decode)
            .consumerMainThread(JournalSyncPacket::handle)
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

    private static List<JournalEntry> buildEntries(
        ServerPlayer player
    ) {
        List<JournalEntry> result =
            new ArrayList<>();

        for (ResourceLocation questId
                : PlayerQuestData.activeIds(player)) {
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
                    quest.journalSection(),
                    QuestManager.isReadyToTurnIn(
                        player,
                        quest
                    )
                        ? "READY"
                        : "ACTIVE",
                    QuestManager.currentLead(
                        player,
                        quest
                    ),
                    QuestManager.stageTitle(
                        player,
                        quest
                    ),
                    QuestManager.stageNumber(
                        player,
                        quest
                    ),
                    quest.stages().size(),
                    "",
                    QuestManager.journalNotes(
                        player,
                        quest
                    ),
                    QuestManager.progressLines(
                        player,
                        quest
                    ),
                    PlayerQuestData.isTracked(
                        player,
                        questId
                    )
                )
            );
        }

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
}
