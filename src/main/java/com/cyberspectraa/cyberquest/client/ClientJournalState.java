package com.cyberspectraa.cyberquest.client;

import com.cyberspectraa.cyberquest.network.JournalEntry;
import net.minecraft.client.Minecraft;

import java.util.List;

public final class ClientJournalState {
    private static List<JournalEntry> entries =
        List.of();

    private ClientJournalState() {
    }

    public static void apply(
        List<JournalEntry> newEntries,
        boolean open
    ) {
        entries = newEntries == null
            ? List.of()
            : List.copyOf(newEntries);

        if (open) {
            Minecraft.getInstance().setScreen(
                new QuestJournalScreen()
            );
        }
    }

    public static List<JournalEntry> entries() {
        return entries;
    }

    public static void clear() {
        entries = List.of();
    }
}
