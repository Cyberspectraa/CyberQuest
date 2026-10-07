package com.cyberspectraa.cyberquest.registry;

import com.cyberspectraa.cyberquest.CyberQuest;
import com.cyberspectraa.cyberquest.block.GuildBoardBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
        DeferredRegister.create(
            ForgeRegistries.BLOCKS,
            CyberQuest.MOD_ID
        );

    public static final RegistryObject<Block> GUILD_BOARD =
        BLOCKS.register(
            "guild_board",
            () -> new GuildBoardBlock(
                BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
            )
        );

    private ModBlocks() {
    }
}
