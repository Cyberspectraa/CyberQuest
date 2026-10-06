package com.cyberspectraa.cyberquest.network.packet;

import com.cyberspectraa.cyberquest.network.QuestNetwork;
import com.cyberspectraa.cyberquest.quest.QuestManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record RequestJournalPacket() {
    public static void encode(
        RequestJournalPacket packet,
        FriendlyByteBuf buffer
    ) {
    }

    public static RequestJournalPacket decode(
        FriendlyByteBuf buffer
    ) {
        return new RequestJournalPacket();
    }

    public static void handle(
        RequestJournalPacket packet,
        Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context =
            contextSupplier.get();
        ServerPlayer player = context.getSender();

        if (player != null) {
            QuestManager.refreshDynamicObjectives(player);
            QuestNetwork.syncJournal(player, true);
        }

        context.setPacketHandled(true);
    }
}
