package com.cyberspectraa.cyberquest.registry;

import com.cyberspectraa.cyberquest.CyberQuest;
import com.cyberspectraa.cyberquest.item.GuildContractItem;
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

    public static final RegistryObject<Item> GUILD_CONTRACT =
        ITEMS.register("guild_contract", () ->
            new GuildContractItem(new Item.Properties()));

    private ModItems() {
    }
}
