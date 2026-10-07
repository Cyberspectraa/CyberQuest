package com.cyberspectraa.cyberquest.guild;

import com.cyberspectraa.cyberquest.quest.QuestDefinition;
import com.cyberspectraa.cyberquest.quest.QuestObjectiveDefinition;
import com.cyberspectraa.cyberquest.quest.QuestObjectiveType;
import com.cyberspectraa.cyberquest.quest.QuestReward;
import com.cyberspectraa.cyberquest.quest.QuestStageDefinition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record ProceduralQuestOffer(
    ResourceLocation id,
    String source,
    String title,
    String description,
    QuestObjectiveType objectiveType,
    String target,
    String objectiveText,
    int count,
    int durationDays,
    int failureGuildReputation,
    int rewardGuildReputation,
    int vanillaXp,
    long cyberXp,
    int silverCoins
) {
    public ProceduralQuestOffer {
        source = safe(source);
        title = safe(title);
        description = safe(description);
        target = safe(target);
        objectiveText = safe(objectiveText);
        count = Math.max(1, count);
        durationDays = Math.max(0, durationDays);
        failureGuildReputation = Math.max(
            0,
            failureGuildReputation
        );
        rewardGuildReputation = Math.max(
            0,
            rewardGuildReputation
        );
        vanillaXp = Math.max(0, vanillaXp);
        cyberXp = Math.max(0L, cyberXp);
        silverCoins = Math.max(0, silverCoins);
    }

    public QuestDefinition definition() {
        QuestObjectiveDefinition objective =
            new QuestObjectiveDefinition(
                "contract",
                objectiveType,
                objectiveText,
                target,
                count,
                "",
                true,
                "",
                0.0D,
                0.0D,
                0.0D,
                5.0D
            );

        QuestStageDefinition stage =
            new QuestStageDefinition(
                "contract",
                "Guild Contract",
                objectiveText,
                List.of(description),
                List.of(objective)
            );

        List<QuestReward.ItemReward> items =
            new ArrayList<>();

        if (silverCoins > 0) {
            items.add(
                new QuestReward.ItemReward(
                    new ResourceLocation(
                        "cybernpc",
                        "silver_coin"
                    ),
                    silverCoins
                )
            );
        }

        QuestReward reward =
            new QuestReward(
                cyberXp,
                vanillaXp,
                rewardGuildReputation,
                "",
                items
            );

        return new QuestDefinition(
            id,
            title,
            description,
            "guild",
            "journal",
            "The guild records the contract as fulfilled.",
            false,
            false,
            List.of(),
            1,
            1,
            List.of(),
            List.of(),
            "",
            "",
            "",
            "",
            durationDays,
            failureGuildReputation,
            List.of(stage),
            reward
        );
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Id", id.toString());
        tag.putString("Source", source);
        tag.putString("Title", title);
        tag.putString(
            "Description",
            description
        );
        tag.putString(
            "ObjectiveType",
            objectiveType.name()
        );
        tag.putString("Target", target);
        tag.putString(
            "ObjectiveText",
            objectiveText
        );
        tag.putInt("Count", count);
        tag.putInt(
            "DurationDays",
            durationDays
        );
        tag.putInt(
            "FailureGuildReputation",
            failureGuildReputation
        );
        tag.putInt(
            "RewardGuildReputation",
            rewardGuildReputation
        );
        tag.putInt(
            "VanillaXp",
            vanillaXp
        );
        tag.putLong(
            "CyberXp",
            cyberXp
        );
        tag.putInt(
            "SilverCoins",
            silverCoins
        );
        return tag;
    }

    public static ProceduralQuestOffer fromTag(
        CompoundTag tag
    ) {
        if (tag == null
                || !tag.contains("Id")
                || !tag.contains(
                    "ObjectiveType"
                )) {
            return null;
        }

        ResourceLocation id =
            ResourceLocation.tryParse(
                tag.getString("Id")
            );

        if (id == null) {
            return null;
        }

        QuestObjectiveType type;

        try {
            type = QuestObjectiveType.valueOf(
                tag.getString(
                    "ObjectiveType"
                )
            );
        } catch (IllegalArgumentException exception) {
            return null;
        }

        return new ProceduralQuestOffer(
            id,
            tag.getString("Source"),
            tag.getString("Title"),
            tag.getString("Description"),
            type,
            tag.getString("Target"),
            tag.getString(
                "ObjectiveText"
            ),
            tag.getInt("Count"),
            tag.getInt("DurationDays"),
            tag.getInt(
                "FailureGuildReputation"
            ),
            tag.getInt(
                "RewardGuildReputation"
            ),
            tag.getInt("VanillaXp"),
            tag.getLong("CyberXp"),
            tag.getInt("SilverCoins")
        );
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
