package com.cyberspectraa.cyberquest.network;

import java.util.List;

public record JournalEntry(
    String questId,
    String title,
    String description,
    String status,
    List<String> objectives
) {
    public JournalEntry {
        questId = questId == null ? "" : questId;
        title = title == null ? "" : title;
        description = description == null ? "" : description;
        status = status == null ? "" : status;
        objectives = objectives == null
            ? List.of()
            : List.copyOf(objectives);
    }
}
