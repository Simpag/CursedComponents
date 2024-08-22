package com.ccteam.cursedcomponents.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class AutoShearer extends Block {
    public AutoShearer(Properties properties) {
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

}
