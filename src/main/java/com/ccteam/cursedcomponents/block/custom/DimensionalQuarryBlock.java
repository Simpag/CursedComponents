package com.ccteam.cursedcomponents.block.custom;

import com.ccteam.cursedcomponents.block.entity.ModBlockEntities;
import com.ccteam.cursedcomponents.block.entity.custom.DimensionalQuarryEntity;
import com.ccteam.cursedcomponents.gui.containers.custom.DimensionalQuarryContainer;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DimensionalQuarryBlock extends BaseEntityBlock {
    public static final MapCodec<DimensionalQuarryBlock> CODEC = simpleCodec(DimensionalQuarryBlock::new);

    public DimensionalQuarryBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        //return super.getTicker(level, state, blockEntityType);
        return createTickerHelper(blockEntityType, ModBlockEntities.DIMENSIONAL_QUARRY_BE.get(), DimensionalQuarryEntity::tick);
        //return blockEntityType == ModBlockEntities.DIMENSIONAL_QUARRY_BE.get() ? DimensionalQuarryEntity::tick : null;
    }


    @Override
    public void appendHoverText(ItemStack stack, Item.@NotNull TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.cursedcomponents.dimensional_quarry.tooltip.1"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new DimensionalQuarryEntity(pos, state);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }


    @Override
    protected void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof DimensionalQuarryEntity dimensionalQuarryEntity) {
                // Drop items stored inside of inventory
                Containers.dropContents(level, pos, dimensionalQuarryEntity.getAllStacks());
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (!level.isClientSide) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(state.getMenuProvider(level, pos));
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(@NotNull BlockState state, Level level, @NotNull BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof DimensionalQuarryEntity dimensionalQuarryEntity) {
            return new SimpleMenuProvider(
                    (containerId, playerInventory, player1) -> new DimensionalQuarryContainer(
                            containerId,
                            playerInventory,
                            ContainerLevelAccess.create(level, pos),
                            dimensionalQuarryEntity.getInventory(),
                            dimensionalQuarryEntity.getQuarryData()
                    ),
                    Component.translatable("menu.cursedcomponents.dimensional_quarry.title")
            );
        }

        return null;
    }

    @Override
    protected float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 1.0f;
    }

    @Override
    protected boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return true;
    }
}
