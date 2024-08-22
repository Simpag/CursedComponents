package com.ccteam.cursedcomponents.item.custom;

import com.ccteam.cursedcomponents.datacomponents.ModDataComponents;
import com.ccteam.cursedcomponents.datacomponents.custom.ItemFilterData;
import com.ccteam.cursedcomponents.gui.containers.custom.ItemFilterContainer;
import com.ccteam.cursedcomponents.stackHandlers.ItemFilterItemStackHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemFilter extends Item {
    public static int FILTER_SIZE = 5;

    public ItemFilter(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand usedHand) {
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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
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
