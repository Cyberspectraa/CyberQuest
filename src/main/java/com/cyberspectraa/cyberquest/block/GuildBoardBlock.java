package com.cyberspectraa.cyberquest.block;

import com.cyberspectraa.cyberquest.guild.GuildContractGenerator;
import com.cyberspectraa.cyberquest.guild.GuildContractManager;
import com.cyberspectraa.cyberquest.guild.GuildBoardSavedData;
import com.cyberspectraa.cyberquest.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public final class GuildBoardBlock extends Block {
    public static final DirectionProperty FACING =
        BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<GuildBoardPart> PART =
        EnumProperty.create("part", GuildBoardPart.class);
    public static final BooleanProperty HAS_PAPER =
        BooleanProperty.create("has_paper");

    private static final VoxelShape NORTH =
        Block.box(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SOUTH =
        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 4.0D);
    private static final VoxelShape WEST =
        Block.box(12.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape EAST =
        Block.box(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 16.0D);

    public GuildBoardBlock(Properties properties) {
        super(properties);
        registerDefaultState(
            stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, GuildBoardPart.BOTTOM_LEFT)
                .setValue(HAS_PAPER, false)
        );
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getClickedFace();

        if (facing.getAxis() == Direction.Axis.Y) {
            return null;
        }

        BlockPos master = context.getClickedPos();

        if (!canPlaceBoard(context, master, facing)) {
            return null;
        }

        return defaultBlockState()
            .setValue(FACING, facing)
            .setValue(PART, GuildBoardPart.BOTTOM_LEFT)
            .setValue(HAS_PAPER, false);
    }

    @Override
    public void setPlacedBy(
        Level level,
        BlockPos pos,
        BlockState state,
        @Nullable LivingEntity placer,
        ItemStack stack
    ) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.isClientSide) {
            return;
        }

        Direction facing = state.getValue(FACING);
        int offerCount = level instanceof ServerLevel serverLevel
            ? GuildContractGenerator.offers(serverLevel, pos).size()
            : 0;

        for (GuildBoardPart part : GuildBoardPart.values()) {
            BlockPos partPos = positionFor(pos, facing, part);
            BlockState partState = defaultBlockState()
                .setValue(FACING, facing)
                .setValue(PART, part)
                .setValue(HAS_PAPER, part.slot() < offerCount && !GuildBoardSavedData.get((ServerLevel) level).isRemoved(pos, part.slot()));

            level.setBlock(partPos, partState, 3);
        }

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.scheduleTick(pos, this, 200);
        }
    }

    @Override
    public void tick(
        BlockState state,
        ServerLevel level,
        BlockPos pos,
        RandomSource random
    ) {
        if (!state.is(this)
                || !state.getValue(PART).isMaster()) {
            return;
        }

        refreshPapers(level, pos, state.getValue(FACING));
        level.scheduleTick(pos, this, 200);
    }

    @Override
    public InteractionResult use(
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hit
    ) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!(player instanceof ServerPlayer serverPlayer)
                || !(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }

        Direction facing = state.getValue(FACING);
        GuildBoardPart part = state.getValue(PART);
        BlockPos master = masterPosition(pos, facing, part);

        refreshPapers(serverLevel, master, facing);

        BlockState refreshed = level.getBlockState(pos);

        if (!refreshed.is(this)
                || !refreshed.getValue(HAS_PAPER)) {
            serverPlayer.displayClientMessage(
                Component.literal("No contract is pinned here."),
                true
            );
            return InteractionResult.CONSUME;
        }

        GuildContractManager.openBoard(
            serverPlayer,
            master,
            part.slot()
        );

        return InteractionResult.CONSUME;
    }

    @Override
    public void playerWillDestroy(
        Level level,
        BlockPos pos,
        BlockState state,
        Player player
    ) {
        if (!level.isClientSide
                && !player.getAbilities().instabuild) {
            popResource(
                level,
                masterPosition(
                    pos,
                    state.getValue(FACING),
                    state.getValue(PART)
                ),
                new ItemStack(ModItems.GUILD_BOARD.get())
            );
        }

        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onRemove(
        BlockState state,
        Level level,
        BlockPos pos,
        BlockState newState,
        boolean movedByPiston
    ) {
        if (!level.isClientSide && !newState.is(this)) {
            Direction facing =
                state.getValue(FACING);
            GuildBoardPart part =
                state.getValue(PART);
            BlockPos master =
                masterPosition(
                    pos,
                    facing,
                    part
                );

            if (part.isMaster()) {
                removeOtherParts(
                    level,
                    master,
                    facing,
                    pos
                );
            } else if (level.getBlockState(master)
                    .is(this)) {
                level.setBlock(
                    master,
                    Blocks.AIR.defaultBlockState(),
                    35
                );
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void neighborChanged(
        BlockState state,
        Level level,
        BlockPos pos,
        Block neighborBlock,
        BlockPos neighborPos,
        boolean movedByPiston
    ) {
        super.neighborChanged(
            state,
            level,
            pos,
            neighborBlock,
            neighborPos,
            movedByPiston
        );

        if (level.isClientSide) {
            return;
        }

        Direction facing = state.getValue(FACING);

        if (!hasWallSupport(level, pos, facing)) {
            BlockPos master = masterPosition(
                pos,
                facing,
                state.getValue(PART)
            );
            BlockState masterState =
                level.getBlockState(master);

            if (masterState.is(this)
                    && masterState.getValue(PART)
                        .isMaster()) {
                popResource(
                    level,
                    master,
                    new ItemStack(
                        ModItems.GUILD_BOARD.get()
                    )
                );

                level.setBlock(
                    master,
                    Blocks.AIR.defaultBlockState(),
                    35
                );
            }
        }
    }

    @Override
    public boolean canSurvive(
        BlockState state,
        LevelReader level,
        BlockPos pos
    ) {
        return hasWallSupport(
            level,
            pos,
            state.getValue(FACING)
        );
    }

    @Override
    public VoxelShape getShape(
        BlockState state,
        BlockGetter level,
        BlockPos pos,
        CollisionContext context
    ) {
        return switch (state.getValue(FACING)) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
            default -> Shapes.block();
        };
    }

    @Override
    protected void createBlockStateDefinition(
        StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(FACING, PART, HAS_PAPER);
    }

    private void refreshPapers(
        ServerLevel level,
        BlockPos master,
        Direction facing
    ) {
        BlockState masterState = level.getBlockState(master);

        if (!masterState.is(this)
                || !masterState.getValue(PART).isMaster()) {
            return;
        }

        int offerCount =
            GuildContractGenerator.offers(level, master).size();
        GuildBoardSavedData taken = GuildBoardSavedData.get(level);

        for (GuildBoardPart part : GuildBoardPart.values()) {
            BlockPos partPos = positionFor(master, facing, part);
            BlockState current = level.getBlockState(partPos);

            if (!current.is(this)
                    || current.getValue(FACING) != facing
                    || current.getValue(PART) != part) {
                continue;
            }

            boolean shouldHavePaper =
                part.slot() < offerCount && !taken.isRemoved(master, part.slot());

            if (current.getValue(HAS_PAPER) != shouldHavePaper) {
                level.setBlock(
                    partPos,
                    current.setValue(HAS_PAPER, shouldHavePaper),
                    3
                );
            }
        }
    }

    private static boolean canPlaceBoard(
        BlockPlaceContext context,
        BlockPos master,
        Direction facing
    ) {
        Level level = context.getLevel();

        for (GuildBoardPart part : GuildBoardPart.values()) {
            BlockPos partPos = positionFor(master, facing, part);

            if (!level.getBlockState(partPos).canBeReplaced(context)
                    || !hasWallSupport(level, partPos, facing)) {
                return false;
            }
        }

        return true;
    }

    private static boolean hasWallSupport(
        LevelReader level,
        BlockPos pos,
        Direction facing
    ) {
        BlockPos supportPos =
            pos.relative(facing.getOpposite());

        return level.getBlockState(supportPos)
            .isFaceSturdy(level, supportPos, facing);
    }

    private static void removeOtherParts(
        Level level,
        BlockPos master,
        Direction facing,
        BlockPos removedPos
    ) {
        for (GuildBoardPart part : GuildBoardPart.values()) {
            BlockPos partPos = positionFor(master, facing, part);

            if (partPos.equals(removedPos)) {
                continue;
            }

            if (level.getBlockState(partPos)
                    .is(com.cyberspectraa.cyberquest.registry.ModBlocks.GUILD_BOARD.get())) {
                level.setBlock(
                    partPos,
                    Blocks.AIR.defaultBlockState(),
                    35
                );
            }
        }
    }

    public static BlockPos masterPosition(
        BlockPos partPos,
        Direction facing,
        GuildBoardPart part
    ) {
        Direction right =
            facing.getCounterClockWise();

        return partPos
            .relative(right, -part.x())
            .below(part.y());
    }

    public static BlockPos positionFor(
        BlockPos master,
        Direction facing,
        GuildBoardPart part
    ) {
        Direction right =
            facing.getCounterClockWise();

        return master
            .relative(right, part.x())
            .above(part.y());
    }
}
