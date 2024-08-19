package com.ccteam.cursedcomponents.entity.custom;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.Goal;

public class LuckyParrotLandOnShoulderGoal extends Goal {
    private final LuckyParrot entity;
    private ServerPlayer owner;
    private boolean isSittingOnShoulder;

    public LuckyParrotLandOnShoulderGoal(LuckyParrot entity) {
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        ServerPlayer serverplayer = (ServerPlayer)this.entity.getOwner();
        boolean flag = serverplayer != null;
//                && !serverplayer.isSpectator()
//                && !serverplayer.getAbilities().flying
//                && !serverplayer.isInWater()
//                && !serverplayer.isInPowderSnow;
        return !this.entity.isOrderedToSit() && flag && this.entity.canSitOnShoulder();
    }

    @Override
    public boolean isInterruptable() {
        return !this.isSittingOnShoulder;
    }

    @Override
    public void start() {
        this.owner = (ServerPlayer)this.entity.getOwner();
        this.isSittingOnShoulder = false;
    }

    @Override
    public void tick() {
        if (!this.isSittingOnShoulder && !this.entity.isInSittingPose() && !this.entity.isLeashed()) {
            if (this.entity.getBoundingBox().intersects(this.owner.getBoundingBox())) {
                this.isSittingOnShoulder = this.entity.setEntityOnShoulder(this.owner);
            }
        }
    }
}
