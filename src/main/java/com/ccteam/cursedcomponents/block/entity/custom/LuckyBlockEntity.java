package com.ccteam.cursedcomponents.block.entity.custom;

import com.ccteam.cursedcomponents.block.ModBlocks;
import com.ccteam.cursedcomponents.block.custom.LuckyBlock;
import com.ccteam.cursedcomponents.block.custom.RollOutcome;
import com.ccteam.cursedcomponents.block.entity.ModBlockEntities;
import com.ccteam.cursedcomponents.entity.ModEntities;
import com.ccteam.cursedcomponents.entity.custom.LuckyParrot;
import com.ccteam.cursedcomponents.network.toClient.LuckyBlockInteractionPayload;
import com.ccteam.cursedcomponents.villager.CustomVillagerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LuckyBlockEntity extends BlockEntity {

    private static final float diceRotationSpeed = 40f;
    private static final int MAX_SPIN_TICKS = 20;
    private static final int BASE_TINT = FastColor.ARGB32.color(0, 0xFFFFFF);

    private float diceRotation;
    private boolean isSpinning;
    private int spinTicks;
    private Player activatingPlayer;
    private int tintColor;

    private RollOutcome rollOutcome;
    private Runnable dropRunnable;

    public LuckyBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.LUCKY_BLOCK_BE.get(), pos, blockState);
        this.isSpinning = false;
        this.diceRotation = 0;
        this.spinTicks = 0;
        this.tintColor = BASE_TINT;
    }

    public void startSpinning(Player activatingPlayer) {
        this.isSpinning = true;
        this.spinTicks = MAX_SPIN_TICKS;
        this.activatingPlayer = activatingPlayer;
        this.tintColor = BASE_TINT;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, LuckyBlockEntity entity) {
        entity.spinTicks--;

        if (level.isClientSide) {
            if (entity.isSpinning && entity.rollOutcome != null) {
                entity.diceRotation = (entity.diceRotation + diceRotationSpeed) % 360;
                int alpha = (int) Math.clamp((((MAX_SPIN_TICKS - entity.spinTicks) / (float) MAX_SPIN_TICKS) * 255), 0, 255);
                int whiteWithAlpha = FastColor.ARGB32.color(alpha, 255, 255, 255);
                entity.tintColor = FastColor.ARGB32.multiply(whiteWithAlpha, entity.rollOutcome.getColor());
            }
        } else {
            if (entity.isSpinning && entity.spinTicks <= 0) {
                entity.isSpinning = false;
                BlockState luckyBlock = level.getBlockState(pos);
                if (luckyBlock.getBlock() instanceof LuckyBlock) {
                    luckyBlock.getBlock().playerDestroy(level, entity.activatingPlayer, pos, luckyBlock, null, entity.activatingPlayer.getMainHandItem());
                    level.destroyBlock(pos, false, entity.activatingPlayer);
                }
            }
        }
    }

    public void rollAndSetDropRunnable(Level world, Player player, BlockPos pos) {
        if (world.isClientSide)
            return;

        float luck = 1.0f; // TODO: Use luck modifiers
        float r = world.getRandom().nextFloat() * luck;

        if (r < 1/100.0 * luck) {
            rollOutcome = RollOutcome.VERY_UNLUCKY;
        }
        else if (r < 20/100.0 * luck) {
            rollOutcome = RollOutcome.UNLUCKY;
        }
        else if (r < 80/100.0 * luck) {
            rollOutcome = RollOutcome.NORMAL;
        }
        else {
            rollOutcome = RollOutcome.LUCKY;
        }

        dropRunnable =  switch (rollOutcome) {
            case VERY_UNLUCKY -> () -> carpetBomb(world, player);
            case UNLUCKY -> () -> dropUnlucky(world, player, pos);
            case NORMAL -> () -> dropNormal(world, pos);
            case LUCKY -> () -> dropLucky(world, player, pos);
        };

        PacketDistributor.sendToPlayer((ServerPlayer) player, new LuckyBlockInteractionPayload(rollOutcome, pos));
    }

    public void runDrop() {
        dropRunnable.run();
    }

    private void dropUnlucky(Level world, Player player, BlockPos pos) {
        List<Runnable> dropTasks = Arrays.asList(
                () -> spawnSkeletons(world, pos),
                () -> spawnAnvilTrap(world, player)
        );
        int r = new Random().nextInt(dropTasks.size());
        dropTasks.get(r).run();
    }

    private void dropNormal(Level world, BlockPos pos) {
        List<Runnable> dropTasks = Arrays.asList(
                () -> dropGoldTools(world, pos),
                () -> dropGoldArmor(world, pos),
                () -> dropEnderChest(world, pos),
                () -> dropSaddles(world, pos),
                () -> dropPotatoes(world, pos),
                () -> dropFishingEquipment(world, pos),
                () -> dropBooks(world, pos),
                () -> dropBuckets(world, pos)
        );
        int r = new Random().nextInt(dropTasks.size());
        dropTasks.get(r).run();
    }

    private void dropLucky(Level world, Player player, BlockPos pos) {
        List<Runnable> dropTasks = Arrays.asList(
                () -> constructBlockTower(world, player, pos),
                () -> spawnLuckyVillager(world, pos),
                () -> hurlBottlesOfEnchanting(world, pos),
//                () -> spawnLuckyParrot(world, pos), // Deactivated until fixed
                () -> dropEndPortalFrames(world, pos),
                () -> spawnLuckyPyramid(world, player, pos)
        );
        int r = new Random().nextInt(dropTasks.size());
        dropTasks.get(r).run();
    }

    private void spawnAnvilTrap(Level world, Player player) {
        BlockPos playerOnPos = player.getOnPos();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = 1; y <= 2; y++) {
                    if (x == 0 && z == 0) {
                        continue;
                    }
                    BlockPos newPos = playerOnPos.offset(x, y, z);
                    world.setBlock(newPos, Blocks.IRON_BARS.defaultBlockState(), 3);
                }
            }
        }
        int dropHeight = 40;
        for (int y = 1; y < dropHeight; y++) {
            world.setBlock(playerOnPos.above(y), Blocks.AIR.defaultBlockState(), 3);
        }
        world.setBlock(playerOnPos.above(dropHeight), Blocks.ANVIL.defaultBlockState(), 3);
    }

    private void carpetBomb(Level world, Player player) {
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                BlockPos newPos = player.getOnPos().offset(x, 20, z);
                PrimedTnt tnt = new PrimedTnt(world, newPos.getX(), newPos.getY(), newPos.getZ(), null);
                tnt.setFuse(60);
                world.addFreshEntity(tnt);
            }
        }
    }

    private void spawnSkeletons(Level world, BlockPos pos) {
        int[] xOffsets = new int[] { -4, 4, 0, 0 };
        int[] zOffsets = new int[] { 0, 0, -4, 4 };

        for (int i = 0; i < xOffsets.length; i++) {
            Skeleton skeleton = EntityType.SKELETON.create(world);
            if (skeleton == null)
                return;

            skeleton.setPos(pos.getX() + xOffsets[i], pos.getY(), pos.getZ() + zOffsets[i]);
            world.addFreshEntity(skeleton);
        }
    }

    private void dropGoldArmor(Level world, BlockPos pos) {
        dropItemStacks(world, pos, new ItemStack[] {
                new ItemStack(Items.GOLDEN_HELMET),
                new ItemStack(Items.GOLDEN_CHESTPLATE),
                new ItemStack(Items.GOLDEN_LEGGINGS),
                new ItemStack(Items.GOLDEN_BOOTS)
        });
    }

    private void dropGoldTools(Level world, BlockPos pos) {
        dropItemStacks(world, pos, new ItemStack[] {
                new ItemStack(Items.GOLDEN_SHOVEL),
                new ItemStack(Items.GOLDEN_PICKAXE),
                new ItemStack(Items.GOLDEN_AXE),
                new ItemStack(Items.GOLDEN_HOE),
                new ItemStack(Items.GOLDEN_SWORD)
        });
    }

    private void dropEnderChest(Level world, BlockPos pos) {
        world.setBlock(pos, Blocks.ENDER_CHEST.defaultBlockState(), 3);
    }

    private void dropSaddles(Level world, BlockPos pos) {
        dropItemStacks(world, pos, new ItemStack[] {
                new ItemStack(Items.SADDLE),
                new ItemStack(Items.SADDLE)
        });
    }

    private void dropPotatoes(Level world, BlockPos pos) {
        dropItemStacks(world, pos, new ItemStack[] {
                new ItemStack(Items.POTATO, 32),
                new ItemStack(Items.BAKED_POTATO, 16),
                new ItemStack(Items.POISONOUS_POTATO, 4),
        });
    }

    private void dropFishingEquipment(Level world, BlockPos pos) {
        dropItemStacks(world, pos, new ItemStack[] {
                new ItemStack(Items.FISHING_ROD, 1),
                new ItemStack(Items.COD_BUCKET, 1),
                new ItemStack(Items.COOKED_SALMON, 16),
                new ItemStack(Items.PUFFERFISH, 4),
        });
    }

    private void dropBooks(Level world, BlockPos pos) {
        ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantmentHelper.enchantItem(world.random, enchantedBook, 12, world.registryAccess(),
                world.registryAccess().registryOrThrow(Registries.ENCHANTMENT)
                        .getTag(EnchantmentTags.IN_ENCHANTING_TABLE));
        dropItemStacks(world, pos, new ItemStack[] {
                new ItemStack(Items.BOOKSHELF, 7),
                new ItemStack(Items.BOOK, 32),
                enchantedBook
        });
    }

    private void dropBuckets(Level world, BlockPos pos) {
        dropItemStacks(world, pos, new ItemStack[] {
                new ItemStack(Items.BUCKET, 3),
                new ItemStack(Items.WATER_BUCKET, 1),
                new ItemStack(Items.LAVA_BUCKET, 1),
                new ItemStack(Items.MILK_BUCKET, 1)
        });
    }

    private void constructBlockTower(Level world, Player player, BlockPos pos) {
        int height = 30;
        int startOffset = (player.getOnPos() == pos) ? 2 : 0;

        for (int i = startOffset; i < height; i++) {
            BlockPos newPos = pos.above(i + 1);
            world.setBlock(newPos, Blocks.STONE.defaultBlockState(), 3);
        }
        world.setBlock(pos.above(height + 1), Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
        world.setBlock(pos.above(height + 2), Blocks.EMERALD_BLOCK.defaultBlockState(), 3);
        world.setBlock(pos.above(height + 3), Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
    }

    private void spawnLuckyParrot(Level world, BlockPos pos) {
        LuckyParrot luckyParrot = ModEntities.LUCKY_PARROT.get().create(world);
        luckyParrot.setPos(pos.getX(), pos.getY(), pos.getZ());
        world.addFreshEntity(luckyParrot);
    }

    private void spawnLuckyVillager(Level world, BlockPos pos) {
        Villager luckyVillager = CustomVillagerManager.createLuckyVillager(world);
        luckyVillager.setPos(pos.getX(), pos.getY(), pos.getZ());
        world.addFreshEntity(luckyVillager);
    }

    private void hurlBottlesOfEnchanting(Level world, BlockPos pos) {
        Direction facing = Direction.UP;
        for (int i = 0; i < 50; i++) {
            double d = world.random.nextDouble();
            throwExperienceBottle(world, pos, facing, d, 60);
        }
    }

    private void throwExperienceBottle(Level world, BlockPos pos, Direction facing, double d, int speed) {
        ThrownExperienceBottle experienceBottle = new ThrownExperienceBottle(world, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
        experienceBottle.setDeltaMovement(
                world.random.triangle((double)facing.getStepX() * d, 0.0172275 * (double)speed),
                world.random.triangle(0.2, 0.0172275 * (double)speed),
                world.random.triangle((double)facing.getStepZ() * d, 0.0172275 * (double)speed)
        );
        world.addFreshEntity(experienceBottle);
    }

    private void dropEndPortalFrames(Level world, BlockPos pos) {
        int numFrames = world.random.nextInt(5) + 2;
        dropItemStacks(world, pos, new ItemStack[] {
                new ItemStack(Items.END_PORTAL_FRAME, numFrames)
        });
    }

    private void spawnLuckyPyramid(Level world, Player player, BlockPos pos) {
        int baseSize = 5;
        int height = baseSize / 2 + 1;

        BlockPos playerPos = player.blockPosition();
        int offsetX = pos.getX() - playerPos.getX();
        offsetX += (offsetX >= 0) ? 2 : 0;
        int offsetZ = pos.getZ() - playerPos.getZ();
        offsetZ += (offsetZ >= 0) ? 2 : 0;

        // Adjust the starting position of the pyramid to avoid overlapping the player
        BlockPos pyramidStartPos = pos.offset(offsetX, 0, offsetZ);

        for (int y = 0; y < height; y++) {
            int layerSize = baseSize - 2 * y;

            for (int x = 0; x < layerSize; x++) {
                for (int z = 0; z < layerSize; z++) {
                    BlockPos newPos = pyramidStartPos.offset(x + y, y, z + y);

                    // Center block at each layer
                    if (x == layerSize / 2 && z == layerSize / 2) {
                        world.setBlock(newPos, ModBlocks.LUCKY_BLOCK.get().defaultBlockState(), 3);
                    } // Edge blocks at the base layer
                    else if (y == 0 && (x == 0 || z == 0 || x == layerSize - 1 || z == layerSize - 1)) {
                        world.setBlock(newPos, Blocks.GOLD_BLOCK.defaultBlockState(), 3);
                    }
                    else {
                        world.setBlock(newPos, Blocks.SANDSTONE.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    private void dropItemStacks(Level world, BlockPos pos, ItemStack[] itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
            world.addFreshEntity(itemEntity);
        }
    }

    public void setRollOutcome(RollOutcome rollOutcome) {
        this.rollOutcome = rollOutcome;
    }

    public float getDiceRotation() {
        return diceRotation;
    }

    public int getTintColor() {
        return tintColor;
    }
}

