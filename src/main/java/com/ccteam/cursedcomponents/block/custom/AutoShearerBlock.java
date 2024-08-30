package com.ccteam.cursedcomponents.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;

public class AutoShearerBlock extends Block {

    public AutoShearerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide)
            return;

        ItemStack stack = new ItemStack(this);

        if (entity instanceof net.neoforged.neoforge.common.IShearable shearable && shearable.isShearable(null, stack, level, pos)) {
            shearable.onSheared(null, stack, level, pos)
                    .forEach(drop -> shearable.spawnShearedDrop(level, pos, drop));
            level.gameEvent(null, GameEvent.SHEAR, pos);
        }
    }


    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.cursedcomponents.auto_shearer.1"));
        tooltip.add(Component.translatable("tooltip.cursedcomponents.auto_shearer.2"));
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1f;
    }
}
