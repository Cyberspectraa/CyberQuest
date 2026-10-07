package com.cyberspectraa.cyberquest.registry;

import com.cyberspectraa.cyberquest.CyberQuest;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(
            ForgeRegistries.ITEMS,
            CyberQuest.MOD_ID
        );

    public static final RegistryObject<Item> GUILD_BOARD =
        ITEMS.register(
            "guild_board",
            () -> new BlockItem(
                ModBlocks.GUILD_BOARD.get(),
                new Item.Properties()
            )
        );

    private ModItems() {
    }
}
