package com.ccteam.cursedcomponents.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

class LuckyParrotWanderGoal extends WaterAvoidingRandomFlyingGoal {
    public LuckyParrotWanderGoal(PathfinderMob mob, double d) {
        super(mob, d);
    }

    @javax.annotation.Nullable
    @Override
    protected Vec3 getPosition() {
        Vec3 vec3 = null;
        if (this.mob.isInWater()) {
            vec3 = LandRandomPos.getPos(this.mob, 15, 15);
        }

        if (this.mob.getRandom().nextFloat() >= this.probability) {
            vec3 = this.getTreePos();
        }

        return vec3 == null ? super.getPosition() : vec3;
    }

    @javax.annotation.Nullable
    private Vec3 getTreePos() {
        BlockPos blockpos = this.mob.blockPosition();
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();

        for (BlockPos blockpos1 : BlockPos.betweenClosed(
                Mth.floor(this.mob.getX() - 3.0),
                Mth.floor(this.mob.getY() - 6.0),
                Mth.floor(this.mob.getZ() - 3.0),
                Mth.floor(this.mob.getX() + 3.0),
                Mth.floor(this.mob.getY() + 6.0),
                Mth.floor(this.mob.getZ() + 3.0)
        )) {
            if (!blockpos.equals(blockpos1)) {
                BlockState blockstate = this.mob.level().getBlockState(blockpos$mutableblockpos1.setWithOffset(blockpos1, Direction.DOWN));
                boolean flag = blockstate.getBlock() instanceof LeavesBlock || blockstate.is(BlockTags.LOGS);
                if (flag
                        && this.mob.level().isEmptyBlock(blockpos1)
                        && this.mob.level().isEmptyBlock(blockpos$mutableblockpos.setWithOffset(blockpos1, Direction.UP))) {
                    return Vec3.atBottomCenterOf(blockpos1);
                }
            }
        }

        return null;
    }
}
