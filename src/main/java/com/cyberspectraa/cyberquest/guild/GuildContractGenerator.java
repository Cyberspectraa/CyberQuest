package com.cyberspectraa.cyberquest.guild;

import com.cyberspectraa.cyberquest.CyberQuest;
import com.cyberspectraa.cyberquest.quest.QuestObjectiveType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public final class GuildContractGenerator {
    public static final int OFFER_COUNT = 6;
    public static final int MIN_OFFERS = 4;

    private static final Target[] HUNTS = {
        new Target(
            "minecraft:zombie",
            "zombies",
            "Restless Dead",
            6,
            14,
            1
        ),
        new Target(
            "minecraft:skeleton",
            "skeletons",
            "Bone and Bow",
            5,
            12,
            1
        ),
        new Target(
            "minecraft:spider",
            "spiders",
            "Web-Cutters Wanted",
            5,
            10,
            1
        ),
        new Target(
            "minecraft:creeper",
            "creepers",
            "Powder Kegs",
            3,
            7,
            2
        ),
        new Target(
            "minecraft:drowned",
            "drowned",
            "Dead from the Water",
            4,
            9,
            2
        ),
        new Target(
            "minecraft:pillager",
            "pillagers",
            "Raiders on the Road",
            4,
            8,
            3
        ),
        new Target(
            "minecraft:witch",
            "witches",
            "A Witching Problem",
            2,
            5,
            3
        )
    };

    private static final Target[] SUPPLIES = {
        new Target(
            "minecraft:oak_log",
            "oak logs",
            "Timber for the Guild",
            16,
            32,
            1
        ),
        new Target(
            "minecraft:spruce_log",
            "spruce logs",
            "Northern Timber",
            16,
            32,
            1
        ),
        new Target(
            "minecraft:coal",
            "coal",
            "Keep the Hearths Lit",
            12,
            28,
            1
        ),
        new Target(
            "minecraft:iron_ingot",
            "iron ingots",
            "Iron for the Armoury",
            6,
            16,
            2
        ),
        new Target(
            "minecraft:leather",
            "leather",
            "Leather for the Quartermaster",
            8,
            18,
            2
        ),
        new Target(
            "minecraft:string",
            "string",
            "Bowyers Need String",
            10,
            24,
            1
        )
    };

    private GuildContractGenerator() {
    }

    public static List<ProceduralQuestOffer> offers(
        ServerLevel level,
        BlockPos boardPos
    ) {
        long day =
            level.getDayTime() / 24000L;
        long seed =
            level.getSeed()
                ^ boardPos.asLong()
                ^ (day
                    * 0x9E3779B97F4A7C15L);

        RandomSource random =
            RandomSource.create(seed);

        int offerCount =
            MIN_OFFERS
                + random.nextInt(
                    OFFER_COUNT
                        - MIN_OFFERS
                        + 1
                );

        List<ProceduralQuestOffer> offers =
            new ArrayList<>();

        for (int slot = 0;
                slot < offerCount;
                slot++) {
            boolean hunt =
                slot % 2 == 0
                    || random.nextBoolean();

            Target target = hunt
                ? HUNTS[
                    random.nextInt(
                        HUNTS.length
                    )
                ]
                : SUPPLIES[
                    random.nextInt(
                        SUPPLIES.length
                    )
                ];

            int count = target.minimum()
                + random.nextInt(
                    target.maximum()
                        - target.minimum()
                        + 1
                );

            boolean timed =
                random.nextFloat() < 0.65F;
            int durationDays = timed
                ? 1 + random.nextInt(2)
                : 0;

            int failurePenalty = timed
                ? 3 + target.difficulty() * 2
                : 0;

            int rewardReputation =
                2 + target.difficulty()
                    + (timed ? 2 : 0);
            int vanillaXp =
                12
                    + count
                    * target.difficulty();
            long cyberXp =
                20L
                    + (long) count
                        * target.difficulty()
                        * 2L;
            int silver =
                4
                    + target.difficulty() * 3
                    + count / 2
                    + (timed ? 4 : 0);

            ResourceLocation id =
                new ResourceLocation(
                    CyberQuest.MOD_ID,
                    "guild/"
                        + day
                        + "/"
                        + Long.toUnsignedString(
                            boardPos.asLong(),
                            36
                        )
                        + "/"
                        + slot
                );

            String objective = hunt
                ? "Defeat "
                    + count
                    + " "
                    + target.label()
                    + "."
                : "Gather "
                    + count
                    + " "
                    + target.label()
                    + ".";

            String description = hunt
                ? "A guild notice calls for adventurers to deal with "
                    + target.label()
                    + " troubling the roads and settlements."
                : "The guild quartermaster has posted a request for "
                    + target.label()
                    + ".";

            offers.add(
                new ProceduralQuestOffer(
                    id,
                    "guild_board",
                    target.title(),
                    description,
                    hunt
                        ? QuestObjectiveType.KILL
                        : QuestObjectiveType.COLLECT,
                    target.id(),
                    objective,
                    count,
                    durationDays,
                    failurePenalty,
                    rewardReputation,
                    vanillaXp,
                    cyberXp,
                    silver
                )
            );
        }

        return List.copyOf(offers);
    }

    private record Target(
        String id,
        String label,
        String title,
        int minimum,
        int maximum,
        int difficulty
    ) {
    }
}
