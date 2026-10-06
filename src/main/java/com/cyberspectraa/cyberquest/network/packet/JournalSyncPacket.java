package com.cyberspectraa.cyberquest.network.packet;

import com.cyberspectraa.cyberquest.client.ClientJournalState;
import com.cyberspectraa.cyberquest.network.JournalEntry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record JournalSyncPacket(
    boolean open,
    List<JournalEntry> entries
) {
    public JournalSyncPacket {
        entries = entries == null
            ? List.of()
            : List.copyOf(entries);
    }

    public static void encode(
        JournalSyncPacket packet,
        FriendlyByteBuf buffer
    ) {
        buffer.writeBoolean(packet.open());
        buffer.writeVarInt(packet.entries().size());

        for (JournalEntry entry : packet.entries()) {
            buffer.writeUtf(entry.questId(), 128);
            buffer.writeUtf(entry.title(), 256);
            buffer.writeUtf(entry.description(), 2048);
            buffer.writeUtf(entry.status(), 32);
            buffer.writeVarInt(
                entry.objectives().size()
            );

            for (String objective
                    : entry.objectives()) {
                buffer.writeUtf(objective, 512);
            }
        }
    }

    public static JournalSyncPacket decode(
        FriendlyByteBuf buffer
    ) {
        boolean open = buffer.readBoolean();
        int size = Math.min(
            512,
            Math.max(0, buffer.readVarInt())
        );

        List<JournalEntry> entries =
            new ArrayList<>();

        for (int i = 0; i < size; i++) {
            String id = buffer.readUtf(128);
            String title = buffer.readUtf(256);
            String description =
                buffer.readUtf(2048);
            String status = buffer.readUtf(32);

            int objectiveCount = Math.min(
                128,
                Math.max(0, buffer.readVarInt())
            );

            List<String> objectives =
                new ArrayList<>();

            for (int objective = 0;
                    objective < objectiveCount;
                    objective++) {
                objectives.add(
                    buffer.readUtf(512)
                );
            }

            entries.add(
                new JournalEntry(
                    id,
                    title,
                    description,
                    status,
                    objectives
                )
            );
        }

        return new JournalSyncPacket(
            open,
            entries
        );
    }

    public static void handle(
        JournalSyncPacket packet,
        Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context =
            contextSupplier.get();

        DistExecutor.unsafeRunWhenOn(
            Dist.CLIENT,
            () -> () -> ClientJournalState.apply(
                packet.entries(),
                packet.open()
            )
        );

        context.setPacketHandled(true);
    }
}
