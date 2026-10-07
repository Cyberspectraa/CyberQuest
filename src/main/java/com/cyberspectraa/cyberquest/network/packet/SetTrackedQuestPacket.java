package com.cyberspectraa.cyberquest.network.packet;

import com.cyberspectraa.cyberquest.network.QuestNetwork;
import com.cyberspectraa.cyberquest.player.PlayerQuestData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SetTrackedQuestPacket(
    String questId
) {
    public SetTrackedQuestPacket {
        questId = questId == null ? "" : questId;
    }

    public static void encode(
        SetTrackedQuestPacket packet,
        FriendlyByteBuf buffer
    ) {
        buffer.writeUtf(packet.questId(), 128);
    }

    public static SetTrackedQuestPacket decode(
        FriendlyByteBuf buffer
    ) {
        return new SetTrackedQuestPacket(
            buffer.readUtf(128)
        );
    }

    public static void handle(
        SetTrackedQuestPacket packet,
        Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context =
            contextSupplier.get();
        ServerPlayer player = context.getSender();

        if (player != null) {
            ResourceLocation questId =
                packet.questId().isBlank()
                    ? null
                    : ResourceLocation.tryParse(
                        packet.questId()
                    );

            PlayerQuestData.setTrackedQuest(
                player,
                questId
            );
            QuestNetwork.syncJournal(player, false);
        }

        context.setPacketHandled(true);
    }
}
