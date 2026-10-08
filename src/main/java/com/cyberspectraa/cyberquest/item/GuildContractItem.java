package com.cyberspectraa.cyberquest.item;

import com.cyberspectraa.cyberquest.guild.ProceduralQuestOffer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/** An unregistered guild notice carried to a Guild Receptionist. */
public final class GuildContractItem extends Item {
    public static final String CONTRACT_KEY = "GuildContract";

    public GuildContractItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static ItemStack issue(Item item, ProceduralQuestOffer offer, String dimension, long day, long position) {
        ItemStack stack = new ItemStack(item);
        CompoundTag tag = stack.getOrCreateTagElement(CONTRACT_KEY);
        tag.put("Offer", offer.toTag());
        tag.putString("Dimension", dimension);
        tag.putLong("Day", day);
        tag.putLong("BoardPos", position);
        stack.setHoverName(Component.literal(offer.title() + " — Guild Contract"));
        return stack;
    }

    @Nullable
    public static CompoundTag data(ItemStack stack) {
        if (!(stack.getItem() instanceof GuildContractItem)) return null;
        CompoundTag tag = stack.getTagElement(CONTRACT_KEY);
        return tag != null && tag.contains("Offer", Tag.TAG_COMPOUND) ? tag : null;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag data = data(stack);
        if (data == null) {
            tooltip.add(Component.literal("Unregistered notice").withStyle(ChatFormatting.GRAY));
            return;
        }
        ProceduralQuestOffer offer = ProceduralQuestOffer.fromTag(data.getCompound("Offer"));
        if (offer == null) return;
        tooltip.add(Component.literal(offer.objectiveText()).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Deliver to a Guild Receptionist to accept").withStyle(ChatFormatting.GOLD));
        if (offer.durationDays() > 0) {
            tooltip.add(Component.literal("Deadline: " + offer.durationDays() + " Minecraft day(s) after registration")
                .withStyle(ChatFormatting.DARK_RED));
        }
    }
}
