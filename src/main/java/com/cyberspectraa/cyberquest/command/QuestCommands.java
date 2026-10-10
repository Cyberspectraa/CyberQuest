package com.cyberspectraa.cyberquest.command;

import com.cyberspectraa.cyberquest.CyberQuest;
import com.cyberspectraa.cyberquest.compat.CyberNpcQuestCompat;
import com.cyberspectraa.cyberquest.network.QuestNetwork;
import com.cyberspectraa.cyberquest.player.PlayerQuestData;
import com.cyberspectraa.cyberquest.quest.QuestDefinition;
import com.cyberspectraa.cyberquest.quest.QuestManager;
import com.cyberspectraa.cyberquest.quest.QuestRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.stream.Collectors;

@Mod.EventBusSubscriber(
    modid = CyberQuest.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class QuestCommands {
    private static final SuggestionProvider<CommandSourceStack>
        QUEST_SUGGESTIONS =
            (context, builder) -> {
                for (ResourceLocation id
                        : QuestRegistry.snapshot().keySet()) {
                    builder.suggest(id.toString());
                }
                return builder.buildFuture();
            };

    private QuestCommands() {
    }

    @SubscribeEvent
    public static void onRegister(
        RegisterCommandsEvent event
    ) {
        register(event.getDispatcher());
    }

    public static void register(
        CommandDispatcher<CommandSourceStack> dispatcher
    ) {
        dispatcher.register(
            Commands.literal("cyberquest")
                .then(
                    Commands.literal("journal")
                        .executes(context -> openJournal(
                            context.getSource()
                                .getPlayerOrException()
                        ))
                )
                .then(
                    Commands.literal("status")
                        .executes(context -> status(
                            context.getSource(),
                            context.getSource()
                                .getPlayerOrException()
                        ))
                        .then(
                            Commands.argument(
                                    "player",
                                    EntityArgument.player()
                                )
                                .requires(source ->
                                    source.hasPermission(2)
                                )
                                .executes(context -> status(
                                    context.getSource(),
                                    EntityArgument.getPlayer(
                                        context,
                                        "player"
                                    )
                                ))
                        )
                )
                .then(Commands.literal("admin")
                    .requires(source -> source.hasPermission(2))
                .then(
                    Commands.literal("start")
                        .requires(source ->
                            source.hasPermission(2)
                        )
                        .then(
                            Commands.argument(
                                    "player",
                                    EntityArgument.player()
                                )
                                .then(
                                    Commands.argument(
                                            "quest",
                                            StringArgumentType.string()
                                        )
                                        .suggests(
                                            QUEST_SUGGESTIONS
                                        )
                                        .executes(context ->
                                            startQuest(
                                                context.getSource(),
                                                EntityArgument.getPlayer(
                                                    context,
                                                    "player"
                                                ),
                                                StringArgumentType.getString(
                                                    context,
                                                    "quest"
                                                )
                                            )
                                        )
                                )
                        )
                )
                .then(
                    Commands.literal("complete")
                        .requires(source ->
                            source.hasPermission(2)
                        )
                        .then(
                            Commands.argument(
                                    "player",
                                    EntityArgument.player()
                                )
                                .then(
                                    Commands.argument(
                                            "quest",
                                            StringArgumentType.string()
                                        )
                                        .suggests(
                                            QUEST_SUGGESTIONS
                                        )
                                        .executes(context ->
                                            completeQuest(
                                                context.getSource(),
                                                EntityArgument.getPlayer(
                                                    context,
                                                    "player"
                                                ),
                                                StringArgumentType.getString(
                                                    context,
                                                    "quest"
                                                )
                                            )
                                        )
                                )
                        )
                )
                .then(
                    Commands.literal("reset")
                        .requires(source ->
                            source.hasPermission(2)
                        )
                        .then(
                            Commands.argument(
                                    "player",
                                    EntityArgument.player()
                                )
                                .then(
                                    Commands.argument(
                                            "quest",
                                            StringArgumentType.string()
                                        )
                                        .suggests(
                                            QUEST_SUGGESTIONS
                                        )
                                        .executes(context ->
                                            resetQuest(
                                                context.getSource(),
                                                EntityArgument.getPlayer(
                                                    context,
                                                    "player"
                                                ),
                                                StringArgumentType.getString(
                                                    context,
                                                    "quest"
                                                )
                                            )
                                        )
                                )
                        )
                )
                .then(
                    Commands.literal("resetall")
                        .requires(source ->
                            source.hasPermission(2)
                        )
                        .then(
                            Commands.argument(
                                    "player",
                                    EntityArgument.player()
                                )
                                .executes(context -> resetAll(
                                    context.getSource(),
                                    EntityArgument.getPlayer(
                                        context,
                                        "player"
                                    )
                                ))
                        )
                )
                )
                .then(npcCommands())
        );
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack>
        npcCommands() {
        return Commands.literal("npc")
            .requires(source ->
                source.hasPermission(2)
            )
            .then(
                Commands.literal("inspect")
                    .then(
                        Commands.argument(
                                "entity",
                                EntityArgument.entity()
                            )
                            .executes(context -> inspectNpc(
                                context.getSource(),
                                EntityArgument.getEntity(
                                    context,
                                    "entity"
                                )
                            ))
                    )
            )
            .then(
                Commands.literal("id")
                    .then(
                        Commands.argument(
                                "entity",
                                EntityArgument.entity()
                            )
                            .then(
                                Commands.argument(
                                        "id",
                                        StringArgumentType.word()
                                    )
                                    .executes(context -> setNpcId(
                                        context.getSource(),
                                        EntityArgument.getEntity(
                                            context,
                                            "entity"
                                        ),
                                        StringArgumentType.getString(
                                            context,
                                            "id"
                                        )
                                    ))
                            )
                    )
            )
            .then(
                Commands.literal("bind")
                    .then(
                        Commands.argument(
                                "entity",
                                EntityArgument.entity()
                            )
                            .then(
                                Commands.argument(
                                        "quest",
                                        StringArgumentType.string()
                                    )
                                    .suggests(
                                        QUEST_SUGGESTIONS
                                    )
                                    .executes(context -> bindQuest(
                                        context.getSource(),
                                        EntityArgument.getEntity(
                                            context,
                                            "entity"
                                        ),
                                        StringArgumentType.getString(
                                            context,
                                            "quest"
                                        )
                                    ))
                            )
                    )
            )
            .then(
                Commands.literal("unbind")
                    .then(
                        Commands.argument(
                                "entity",
                                EntityArgument.entity()
                            )
                            .then(
                                Commands.argument(
                                        "quest",
                                        StringArgumentType.string()
                                    )
                                    .suggests(
                                        QUEST_SUGGESTIONS
                                    )
                                    .executes(context -> unbindQuest(
                                        context.getSource(),
                                        EntityArgument.getEntity(
                                            context,
                                            "entity"
                                        ),
                                        StringArgumentType.getString(
                                            context,
                                            "quest"
                                        )
                                    ))
                            )
                    )
            );
    }

    private static int openJournal(
        ServerPlayer player
    ) {
        QuestManager.refreshDynamicObjectives(player);
        QuestNetwork.syncJournal(player, true);
        return 1;
    }

    private static int status(
        CommandSourceStack source,
        ServerPlayer player
    ) {
        var active =
            PlayerQuestData.activeIds(player);
        var completed =
            PlayerQuestData.completedIds(player);

        source.sendSuccess(
            () -> Component.literal(
                player.getGameProfile().getName()
                    + " — "
                    + active.size() + " active, "
                    + completed.size() + " completed"
            ),
            false
        );

        for (ResourceLocation id : active) {
            QuestDefinition quest =
                QuestRegistry.get(id).orElse(null);

            if (quest == null) {
                continue;
            }

            source.sendSuccess(
                () -> Component.literal(
                    "• " + quest.title()
                        + (QuestManager.isReadyToTurnIn(
                            player,
                            quest
                        )
                            ? " [ready]"
                            : "")
                ),
                false
            );
        }

        return active.size();
    }

    private static int startQuest(
        CommandSourceStack source,
        ServerPlayer player,
        String value
    ) {
        ResourceLocation id = questId(
            source,
            value
        );

        if (id == null) {
            return 0;
        }

        if (!QuestManager.start(player, id, true)) {
            source.sendFailure(
                Component.literal(
                    "Could not start quest " + id
                )
            );
            return 0;
        }

        return 1;
    }

    private static int completeQuest(
        CommandSourceStack source,
        ServerPlayer player,
        String value
    ) {
        ResourceLocation id = questId(
            source,
            value
        );

        if (id == null) {
            return 0;
        }

        if (!PlayerQuestData.isActive(player, id)) {
            QuestManager.start(player, id, true);
        }

        if (!QuestManager.turnIn(player, id, true)) {
            source.sendFailure(
                Component.literal(
                    "Could not complete quest " + id
                )
            );
            return 0;
        }

        return 1;
    }

    private static int resetQuest(
        CommandSourceStack source,
        ServerPlayer player,
        String value
    ) {
        ResourceLocation id = questId(
            source,
            value
        );

        if (id == null) {
            return 0;
        }

        QuestManager.resetQuest(player, id);
        source.sendSuccess(
            () -> Component.literal(
                "Reset quest " + id
                    + " for "
                    + player.getGameProfile().getName()
            ),
            true
        );
        return 1;
    }

    private static int resetAll(
        CommandSourceStack source,
        ServerPlayer player
    ) {
        PlayerQuestData.resetAll(player);
        QuestNetwork.syncJournal(player, false);

        source.sendSuccess(
            () -> Component.literal(
                "Reset all CyberQuest data for "
                    + player.getGameProfile().getName()
            ),
            true
        );

        return 1;
    }

    private static int inspectNpc(
        CommandSourceStack source,
        Entity entity
    ) {
        if (!CyberNpcQuestCompat.isCyberNpc(entity)) {
            source.sendFailure(
                Component.literal(
                    "Target is not a CyberNpc entity."
                )
            );
            return 0;
        }

        String id =
            CyberNpcQuestCompat.npcId(entity);
        String bindings =
            CyberNpcQuestCompat.bindings(entity)
                .stream()
                .map(ResourceLocation::toString)
                .collect(Collectors.joining(", "));

        source.sendSuccess(
            () -> Component.literal(
                "Quest NPC id: "
                    + (id.isBlank() ? "<unset>" : id)
                    + " | type="
                    + (CyberNpcQuestCompat.isQuestNpc(entity)
                        ? "QUEST"
                        : "other")
                    + " | quests="
                    + (bindings.isBlank()
                        ? "<none>"
                        : bindings)
            ),
            false
        );

        return 1;
    }

    private static int setNpcId(
        CommandSourceStack source,
        Entity entity,
        String npcId
    ) {
        if (!CyberNpcQuestCompat.isCyberNpc(entity)) {
            source.sendFailure(
                Component.literal(
                    "Target is not a CyberNpc entity."
                )
            );
            return 0;
        }

        CyberNpcQuestCompat.setNpcId(
            entity,
            npcId
        );

        source.sendSuccess(
            () -> Component.literal(
                "Set CyberQuest NPC id to "
                    + npcId.toLowerCase()
            ),
            true
        );

        return 1;
    }

    private static int bindQuest(
        CommandSourceStack source,
        Entity entity,
        String value
    ) {
        ResourceLocation id = questId(
            source,
            value
        );

        if (id == null) {
            return 0;
        }

        if (!CyberNpcQuestCompat.isCyberNpc(entity)) {
            source.sendFailure(
                Component.literal(
                    "Target is not a CyberNpc entity."
                )
            );
            return 0;
        }

        CyberNpcQuestCompat.bind(entity, id);

        source.sendSuccess(
            () -> Component.literal(
                "Bound quest " + id
                    + " to CyberNpc."
            ),
            true
        );

        return 1;
    }

    private static int unbindQuest(
        CommandSourceStack source,
        Entity entity,
        String value
    ) {
        ResourceLocation id = questId(
            source,
            value
        );

        if (id == null) {
            return 0;
        }

        if (!CyberNpcQuestCompat.unbind(
                entity,
                id
        )) {
            source.sendFailure(
                Component.literal(
                    "That quest was not bound to the target."
                )
            );
            return 0;
        }

        source.sendSuccess(
            () -> Component.literal(
                "Unbound quest " + id
                    + " from CyberNpc."
            ),
            true
        );

        return 1;
    }

    private static ResourceLocation questId(
        CommandSourceStack source,
        String value
    ) {
        ResourceLocation id =
            ResourceLocation.tryParse(value);

        if (id == null
                || QuestRegistry.get(id).isEmpty()) {
            source.sendFailure(
                Component.literal(
                    "Unknown quest: " + value
                ).withStyle(ChatFormatting.RED)
            );
            return null;
        }

        return id;
    }
}
