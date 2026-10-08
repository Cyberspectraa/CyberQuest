package com.cyberspectraa.cyberquest.item;

import com.cyberspectraa.cyberquest.network.QuestNetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/** A reusable membership card; details are always resolved on the server. */
public final class GuildCardItem extends Item {
    public GuildCardItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
        Level level, Player player, InteractionHand hand
    ) {
        ItemStack card = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            QuestNetwork.openGuildCard(serverPlayer);
        }
        return InteractionResultHolder.sidedSuccess(card, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Right-click to view your guild standing")
            .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("Issued by the Guild Receptionist")
            .withStyle(ChatFormatting.GRAY));
    }
}
