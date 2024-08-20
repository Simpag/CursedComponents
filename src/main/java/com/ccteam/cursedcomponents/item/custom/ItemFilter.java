package com.ccteam.cursedcomponents.item.custom;

import com.ccteam.cursedcomponents.block.entity.custom.DimensionalQuarryEntity;
import com.ccteam.cursedcomponents.datacomponents.ModDataComponents;
import com.ccteam.cursedcomponents.datacomponents.custom.ItemFilterData;
import com.ccteam.cursedcomponents.gui.containers.custom.DimensionalQuarryContainer;
import com.ccteam.cursedcomponents.gui.containers.custom.ItemFilterContainer;
import com.ccteam.cursedcomponents.stackHandlers.ItemFilterItemStackHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemFilter extends Item {
    public static int FILTER_SIZE = 5;

    public ItemFilter(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        // SHOW GUI
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer)
            serverPlayer.openMenu(getMenuProvider(serverPlayer, usedHand));

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    protected @Nullable MenuProvider getMenuProvider(ServerPlayer serverPlayer, InteractionHand hand) {
        ItemStack stack = serverPlayer.getItemInHand(hand);
        if (stack.getItem() instanceof ItemFilter filter) {
            return new SimpleMenuProvider(
                    (containerId, playerInventory, player1) -> new ItemFilterContainer(
                            containerId,
                            playerInventory,
                            stack
                    ),
                    Component.translatable("menu.cursedcomponents.item_filter.title")
            );
        }

        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.cursedcomponents.item_filter.1"));
        tooltipComponents.add(Component.translatable("tooltip.cursedcomponents.item_filter.2"));

        ItemFilterItemStackHandler inv = stack.getOrDefault(ModDataComponents.ITEM_FILTER_DATA, new ItemFilterData(null)).getInventory(context.registries());
        boolean empty = true;
        for (int i = 0; i < FILTER_SIZE; i++) {
            ItemStack s = inv.getStackInSlot(i);
            if (!s.isEmpty()) {
                tooltipComponents.add(Component.literal("  - ").append(s.getDisplayName()));
                empty = false;
            }
        }

        if (empty)
            tooltipComponents.add(Component.translatable("tooltip.cursedcomponents.item_filter.empty"));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
