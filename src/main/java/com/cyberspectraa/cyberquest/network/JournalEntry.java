package com.cyberspectraa.cyberquest.network;

import java.util.List;

public record JournalEntry(
    String questId,
    String title,
    String description,
    String category,
    String section,
    String status,
    String currentLead,
    String stageTitle,
    int stageNumber,
    int stageCount,
    String completionText,
    List<String> notes,
    List<String> objectives,
    boolean tracked
) {
    public JournalEntry {
        questId = safe(questId);
        title = safe(title);
        description = safe(description);
        category = safe(category);
        section = safe(section);
        status = safe(status);
        currentLead = safe(currentLead);
        stageTitle = safe(stageTitle);
        completionText = safe(completionText);
        stageNumber = Math.max(0, stageNumber);
        stageCount = Math.max(0, stageCount);
        notes = notes == null
            ? List.of()
            : List.copyOf(notes);
        objectives = objectives == null
            ? List.of()
            : List.copyOf(objectives);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
