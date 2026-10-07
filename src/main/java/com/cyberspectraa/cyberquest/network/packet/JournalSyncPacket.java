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
        entries = entries == null ? List.of() : List.copyOf(entries);
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
            buffer.writeUtf(entry.category(), 64);
            buffer.writeUtf(entry.section(), 32);
            buffer.writeUtf(entry.status(), 32);
            buffer.writeUtf(entry.currentLead(), 1024);
            buffer.writeUtf(entry.timer(), 128);
            buffer.writeUtf(entry.stageTitle(), 256);
            buffer.writeVarInt(entry.stageNumber());
            buffer.writeVarInt(entry.stageCount());
            buffer.writeUtf(entry.completionText(), 2048);
            buffer.writeBoolean(entry.tracked());
            writeStrings(buffer, entry.notes(), 128, 1024);
            writeStrings(buffer, entry.objectives(), 128, 512);
        }
    }

    public static JournalSyncPacket decode(
        FriendlyByteBuf buffer
    ) {
        boolean open = buffer.readBoolean();
        int size = Math.min(512, Math.max(0, buffer.readVarInt()));
        List<JournalEntry> entries = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            String id = buffer.readUtf(128);
            String title = buffer.readUtf(256);
            String description = buffer.readUtf(2048);
            String category = buffer.readUtf(64);
            String section = buffer.readUtf(32);
            String status = buffer.readUtf(32);
            String currentLead = buffer.readUtf(1024);
            String timer = buffer.readUtf(128);
            String stageTitle = buffer.readUtf(256);
            int stageNumber = Math.max(0, buffer.readVarInt());
            int stageCount = Math.max(0, buffer.readVarInt());
            String completionText = buffer.readUtf(2048);
            boolean tracked = buffer.readBoolean();
            List<String> notes =
                readStrings(buffer, 128, 1024);
            List<String> objectives =
                readStrings(buffer, 128, 512);

            entries.add(
                new JournalEntry(
                    id,
                    title,
                    description,
                    category,
                    section,
                    status,
                    currentLead,
                    timer,
                    stageTitle,
                    stageNumber,
                    stageCount,
                    completionText,
                    notes,
                    objectives,
                    tracked
                )
            );
        }

        return new JournalSyncPacket(open, entries);
    }

    public static void handle(
        JournalSyncPacket packet,
        Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context = contextSupplier.get();

        DistExecutor.unsafeRunWhenOn(
            Dist.CLIENT,
            () -> () -> ClientJournalState.apply(
                packet.entries(),
                packet.open()
            )
        );

        context.setPacketHandled(true);
    }

    private static void writeStrings(
        FriendlyByteBuf buffer,
        List<String> values,
        int maximumCount,
        int maximumLength
    ) {
        int size = Math.min(maximumCount, values.size());
        buffer.writeVarInt(size);

        for (int i = 0; i < size; i++) {
            buffer.writeUtf(values.get(i), maximumLength);
        }
    }

    private static List<String> readStrings(
        FriendlyByteBuf buffer,
        int maximumCount,
        int maximumLength
    ) {
        int size = Math.min(
            maximumCount,
            Math.max(0, buffer.readVarInt())
        );
        List<String> values = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            values.add(buffer.readUtf(maximumLength));
        }

        return values;
    }
}
