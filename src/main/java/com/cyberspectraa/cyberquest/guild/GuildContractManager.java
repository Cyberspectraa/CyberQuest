package com.cyberspectraa.cyberquest.guild;

import com.cyberspectraa.cyberquest.block.GuildBoardBlock;
import com.cyberspectraa.cyberquest.item.GuildContractItem;
import com.cyberspectraa.cyberquest.network.QuestNetwork;
import com.cyberspectraa.cyberquest.player.PlayerQuestData;
import com.cyberspectraa.cyberquest.quest.QuestManager;
import com.cyberspectraa.cyberquest.registry.ModBlocks;
import com.cyberspectraa.cyberquest.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public final class GuildContractManager {
    public static final int MAX_ACTIVE_CONTRACTS = 3;

    private GuildContractManager() {
    }

    public static void openBoard(ServerPlayer player, BlockPos pos, int slot) {
        if (!(player.level() instanceof ServerLevel level)
                || !validBoard(level, pos)
                || player.distanceToSqr(pos.getX() + 1.5, pos.getY() + 1.0, pos.getZ() + 0.5) > 64.0D
                || GuildBoardSavedData.get(level).isRemoved(pos, slot)) {
            return;
        }
        QuestNetwork.openGuildBoard(player, pos, slot);
    }

    /**
     * Picks up a paper without starting a quest. Because the board is shared,
     * this physically removes its poster for all players until dawn.
     */
    public static boolean accept(ServerPlayer player, BlockPos pos, int slot) {
        if (!(player.level() instanceof ServerLevel level)
                || slot < 0 || slot >= GuildContractGenerator.OFFER_COUNT
                || !validBoard(level, pos)
                || player.distanceToSqr(pos.getX() + 1.5, pos.getY() + 1.0, pos.getZ() + 0.5) > 64.0D) {
            return false;
        }
        GuildBoardSavedData boardData = GuildBoardSavedData.get(level);
        if (boardData.isRemoved(pos, slot)) return false;

        List<ProceduralQuestOffer> offers = GuildContractGenerator.offers(level, pos);
        if (slot >= offers.size()) return false;

        ProceduralQuestOffer offer = offers.get(slot);
        ItemStack paper = GuildContractItem.issue(
            ModItems.GUILD_CONTRACT.get(),
            offer,
            level.dimension().location().toString(),
            level.getDayTime() / 24000L,
            pos.asLong()
        );

        // Never consume a shared notice if the recipient cannot hold the item.
        if (!player.getInventory().add(paper)) {
            player.displayClientMessage(
                Component.literal("Make room in your inventory before taking this notice.")
                    .withStyle(ChatFormatting.YELLOW),
                true
            );
            return false;
        }

        if (!boardData.take(pos, slot)) {
            // All work happens on the server main thread, so this is only
            // a defensive case; undo the granted item if needed.
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack == paper || (GuildContractItem.data(stack) != null
                        && stack.getTag().equals(paper.getTag()))) {
                    stack.shrink(1);
                    break;
                }
            }
            return false;
        }

        BlockState state = level.getBlockState(pos);
        ((GuildBoardBlock) ModBlocks.GUILD_BOARD.get()).refreshPapers(
            level, pos, state.getValue(GuildBoardBlock.FACING)
        );

        player.playNotifySound(SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 0.75F, 0.85F);
        player.displayClientMessage(
            Component.literal("Notice taken. Bring it to a Guild Receptionist to register.")
                .withStyle(ChatFormatting.GOLD),
            true
        );
        player.closeContainer();
        return true;
    }

    public static void receptionistInteraction(ServerPlayer player) {
        ItemStack held = player.getMainHandItem();
        if (held.is(ModItems.GUILD_CONTRACT.get())) {
            registerContract(player, held);
            return;
        }

        int completed = QuestManager.turnInReadyCategory(player, "guild");
        if (completed > 0) {
            player.displayClientMessage(
                Component.literal("The guild has recorded your completed contracts.")
                    .withStyle(ChatFormatting.GOLD),
                true
            );
        } else {
            player.displayClientMessage(
                Component.literal("Hold an unregistered Guild Contract to sign up. Return here to collect completed contract rewards.")
                    .withStyle(ChatFormatting.YELLOW),
                false
            );
        }
    }

    private static boolean registerContract(ServerPlayer player, ItemStack stack) {
        CompoundTag data = GuildContractItem.data(stack);
        if (data == null) {
            player.displayClientMessage(Component.literal("That notice is invalid."), true);
            return false;
        }

        String rawDimension = data.getString("Dimension");
        ResourceLocation dimensionId = ResourceLocation.tryParse(rawDimension);
        if (dimensionId == null || !data.contains("Day", Tag.TAG_LONG)
                || !data.contains("BoardPos", Tag.TAG_LONG)) return false;

        ServerLevel origin = player.server.getLevel(
            ResourceKey.create(Registries.DIMENSION, dimensionId)
        );
        if (origin == null) return false;

        long issuedDay = data.getLong("Day");
        // Can't register a notice dated in the future.
        if (issuedDay < 0 || issuedDay > origin.getDayTime() / 24000L) return false;

        BlockPos boardPos = BlockPos.of(data.getLong("BoardPos"));
        ProceduralQuestOffer requested =
            ProceduralQuestOffer.fromTag(data.getCompound("Offer"));
        if (requested == null) return false;

        // Do not trust player-supplied item NBT: reconstruct the exact offer
        // with the server world's seed, board position and original day.
        ProceduralQuestOffer canonical = null;
        for (ProceduralQuestOffer offer
                : GuildContractGenerator.offersForDay(origin, boardPos, issuedDay)) {
            if (offer.id().equals(requested.id())) {
                canonical = offer;
                break;
            }
        }
        if (canonical == null || !canonical.toTag().equals(requested.toTag())) {
            player.displayClientMessage(
                Component.literal("This contract cannot be verified by the guild.")
                    .withStyle(ChatFormatting.RED),
                true
            );
            return false;
        }

        if (QuestManager.activeCategoryCount(player, "guild") >= MAX_ACTIVE_CONTRACTS) {
            player.displayClientMessage(
                Component.literal("You already have three registered guild contracts.")
                    .withStyle(ChatFormatting.YELLOW),
                true
            );
            return false;
        }

        if (PlayerQuestData.isActive(player, canonical.id())) return false;

        if (!QuestManager.startGenerated(
                player, canonical.definition(), canonical.toTag())) {
            return false;
        }

        // Registration is the moment a deadline starts. Paper is consumed.
        stack.shrink(1);
        player.playNotifySound(SoundEvents.VILLAGER_YES, SoundSource.PLAYERS, 0.8F, 1.1F);
        player.displayClientMessage(
            Component.literal("Contract registered! Your journal has been updated.")
                .withStyle(ChatFormatting.GOLD),
            true
        );
        return true;
    }

    private static boolean validBoard(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.is(ModBlocks.GUILD_BOARD.get())
            && state.getValue(GuildBoardBlock.PART).isMaster();
    }
}
