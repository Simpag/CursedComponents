package com.ccteam.cursedcomponents.block.custom;

import com.ccteam.cursedcomponents.villager.CustomVillagerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Random;

public class LuckyBlock extends Block {

    public LuckyBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (world.isClientSide())
            return;

        super.playerDestroy(world, player, pos, state, blockEntity, tool);
        Random random = new Random();

        float r = random.nextFloat();
        if (r < 0.2)
            dropUnlucky(world, player, pos);
        else if (r < 0.8)
            dropNormal(world, pos);
        else
            dropVeryLucky(world, pos);
    }

    private void dropUnlucky(Level world, Player player, BlockPos pos) {
        if (new Random().nextInt() % 2 == 0)
            spawnSkeletons(world, pos);
        else
            spawnAnvilTrap(world, player, pos);
    }

    private void dropNormal(Level world, BlockPos pos) {
        int r = new Random().nextInt() % 3;
        if (r == 0)
            dropGoldTools(world, pos);
        else if (r == 1)
            dropGoldArmor(world, pos);
        else
            dropEnderChest(world, pos);
    }

    private void dropVeryLucky(Level world, BlockPos pos) {
        int r = new Random().nextInt() % 2;
        if (r == 0)
            constructBlockTower(world, pos);
        else if (r == 1)
            spawnLuckyVillager(world, pos);
        else
            hurlBottlesOfEnchanting(world, pos);
    }

    private void spawnAnvilTrap(Level world, Player player, BlockPos pos) {
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

        world.setBlock(playerOnPos.above(40), Blocks.ANVIL.defaultBlockState(), 3);
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

    private void constructBlockTower(Level world, BlockPos pos) {
        int height = 30;
        for (int i = 0; i < height; i++) {
            BlockPos newPos = pos.above(i + 1);
            world.setBlock(newPos, Blocks.STONE.defaultBlockState(), 3);
        }
        world.setBlock(pos.above(height + 1), Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
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

    private void dropItemStacks(Level world, BlockPos pos, ItemStack[] itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
            world.addFreshEntity(itemEntity);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, java.util.List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
        tooltip.add(Component.literal("A block of luck"));
    }
}