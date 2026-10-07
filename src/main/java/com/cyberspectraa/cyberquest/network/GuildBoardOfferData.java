package com.cyberspectraa.cyberquest.network;

public record GuildBoardOfferData(
    int slot,
    String questId,
    String title,
    String task,
    String reward,
    String timer,
    String penalty,
    String state
) {
    public GuildBoardOfferData {
        questId = safe(questId);
        title = safe(title);
        task = safe(task);
        reward = safe(reward);
        timer = safe(timer);
        penalty = safe(penalty);
        state = safe(state);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
