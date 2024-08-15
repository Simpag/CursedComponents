package com.ccteam.cursedcomponents.entity.custom;

import com.ccteam.cursedcomponents.entity.attachments.ModEntityAttachments;
import com.ccteam.cursedcomponents.keybinds.ModKeyBinds;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class LuckyParrot extends ShoulderRidingEntity implements FlyingAnimal {
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(LuckyParrot.class, EntityDataSerializers.INT);

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    private float flapping = 1.0F;
    private float nextFlap = 1.0F;

    public LuckyParrot(EntityType<? extends LuckyParrot> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new TamableAnimal.TamableAnimalPanicGoal(1.25));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0, 5.0F, 1.0F));
        this.goalSelector.addGoal(2, new LuckyParrotWanderGoal(this, 1.0));
        this.goalSelector.addGoal(3, new LuckyParrotLandOnShoulderGoal(this));
        this.goalSelector.addGoal(3, new FollowMobGoal(this, 1.0, 3.0F, 7.0F));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            if (this.isTame()) {
                this.setOrderedToSit(!this.isOrderedToSit());
            } else {
                this.tame(player);
                this.level().broadcastEntityEvent(this, EntityEvent.TAMING_SUCCEEDED);
            }
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    public boolean setEntityOnShoulder(ServerPlayer player) {
        // TODO: replace with own functionality so we can decide when to dismount
        //super.setEntityOnShoulder(player);
        if (player.isPassenger() || !player.onGround() || player.isInWater() || player.isInPowderSnow) {
            return false;
        } else if (player.getData(ModEntityAttachments.entityPlayerAttachment).isEmpty()) {
            LogUtils.getLogger().info("Set parrot on shoulder");
            player.setData(ModEntityAttachments.entityPlayerAttachment, this.getUUID().toString());
            return true;
        } else {
            return false;
        }
    }

    public void dismountFromShoulder(ServerPlayer player) {
        if(ModKeyBinds.LUCKY_ANIMAL_DISMOUNT.get().isDown()) {
            player.setData(ModEntityAttachments.entityPlayerAttachment, this.getUUID().toString());
        }
    }

    @Override
    public void tick() {
        super.tick();

        if(this.level().isClientSide()) {
            this.setupAnimationStates();
        }

        if (!this.level().isClientSide && this.getOwner() != null) {
            ServerPlayer player = (ServerPlayer) this.getOwner();
            if (player != null && isOnShoulder(player)) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.SLOW_FALLING, 100, 10, false, false));
            }
            dismountFromShoulder(player);
        }
    }

    private boolean isOnShoulder(Player player) {
        return player.getData(ModEntityAttachments.entityPlayerAttachment).equals(this.getUUID().toString());
    }

    /* COPY PASTA */

    @Override
    public void playerTouch(Player player) {
        super.playerTouch(player);
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public boolean canBeLeashed() {
        return true;
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return false;
    }

    @Override
    public boolean canMate(Animal otherAnimal) {
        return false;
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, (double)(0.5F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    private void setupAnimationStates() {
        if(this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 20;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.calculateFlapping();
    }

    private void calculateFlapping() {
        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;
        this.flapSpeed = this.flapSpeed + (float)(!this.onGround() && !this.isPassenger() ? 4 : -1) * 0.3F;
        this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
        if (!this.onGround() && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }

        this.flapping *= 0.9F;
        Vec3 vec3 = this.getDeltaMovement();
        if (!this.onGround() && vec3.y < 0.0) {
            this.setDeltaMovement(vec3.multiply(1.0, 0.6, 1.0));
        }

        this.flap = this.flap + this.flapping * 2.0F;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(VARIANT, 0);
    }

    private int getTypeVariant() {
        return this.entityData.get(VARIANT);
    }

    @Override
    protected boolean canFlyToOwner() {
        return true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Variant", this.getTypeVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.entityData.set(VARIANT, pCompound.getInt("Variant"));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pSpawnType,
                                        @Nullable SpawnGroupData pSpawnGroupData) {
        return super.finalizeSpawn(pLevel, pDifficulty, pSpawnType, pSpawnGroupData);
    }

    /* SOUNDS */
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PARROT_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.PARROT_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PARROT_DEATH;
    }

    @Override
    public boolean isFlying() {
        return !this.onGround();
    }
}
