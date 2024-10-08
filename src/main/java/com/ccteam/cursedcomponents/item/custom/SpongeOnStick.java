package com.ccteam.cursedcomponents.item.custom;

import com.ccteam.cursedcomponents.Config;
import com.ccteam.cursedcomponents.item.ModItems;
import com.ccteam.cursedcomponents.item.base.BasePoweredItem;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.minecraft.world.level.block.Block.dropResources;

public class SpongeOnStick extends BasePoweredItem {
    private static final int MAX_DEPTH = 6;
    private static final int MAX_COUNT = 64;
    private static final Direction[] ALL_DIRECTIONS = Direction.values();

    public SpongeOnStick(Properties properties) {
        super(properties, () -> Config.spongeOnStickCapacity, () -> Config.spongeOnStickUsage);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        if (!level.isClientSide) {
            this.tryAbsorbWater(level, context.getClickedPos(), context.getItemInHand());
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide) {
            this.tryAbsorbWater(level, player.getOnPos().relative(Direction.DOWN), stack);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.cursedcomponents.sponge_on_stick.1", this.getEnergyStored(stack), this.getCapacity()));

        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.cursedcomponents.sponge_on_stick.2"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.cursedcomponents.sponge_on_stick.shift"));
        }
    }


    protected void tryAbsorbWater(Level level, BlockPos pos, ItemStack stack) {
        if (!stack.is(ModItems.SPONGE_ON_STICK))
            return;

        if (this.isOperable(stack) && this.removeWaterBreadthFirstSearch(level, pos)) {
            this.extractEnergy(stack, this.getEnergyUsage(), false);
            level.playSound(null, pos, SoundEvents.SPONGE_ABSORB, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    private boolean removeWaterBreadthFirstSearch(Level level, BlockPos pos) {
        BlockState spongeState = level.getBlockState(pos);
        return BlockPos.breadthFirstTraversal(
                pos,
                MAX_DEPTH,
                MAX_COUNT + 1,
                (p_277519_, p_277492_) -> {
                    for (Direction direction : ALL_DIRECTIONS) {
                        p_277492_.accept(p_277519_.relative(direction));
                    }
                },
                nextPos -> {
                    if (nextPos.equals(pos)) {
                        return true;
                    } else {
                        BlockState blockstate = level.getBlockState(nextPos);
                        FluidState fluidstate = level.getFluidState(nextPos);
                        if (!spongeState.canBeHydrated(level, pos, fluidstate, nextPos)) {
                            return false;
                        } else {
                            if (blockstate.getBlock() instanceof BucketPickup bucketpickup
                                    && !bucketpickup.pickupBlock(null, level, nextPos, blockstate).isEmpty()) {
                                return true;
                            }

                            if (blockstate.getBlock() instanceof LiquidBlock) {
                                level.setBlock(nextPos, Blocks.AIR.defaultBlockState(), 3);
                            } else {
                                if (!blockstate.is(Blocks.KELP)
                                        && !blockstate.is(Blocks.KELP_PLANT)
                                        && !blockstate.is(Blocks.SEAGRASS)
                                        && !blockstate.is(Blocks.TALL_SEAGRASS)) {
                                    return false;
                                }

                                BlockEntity blockentity = blockstate.hasBlockEntity() ? level.getBlockEntity(nextPos) : null;
                                dropResources(blockstate, level, nextPos, blockentity);
                                level.setBlock(nextPos, Blocks.AIR.defaultBlockState(), 3);
                            }
                            return true;
                        }
                    }
                }
        )
                > 1;
    }
}
