package com.ccteam.cursedcomponents.item.custom;

import com.ccteam.cursedcomponents.gui.container.custom.DimensionalQuarryItemFilterContainer;
import com.ccteam.cursedcomponents.item.base.BaseInventoryItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DimensionalQuarryItemFilter extends BaseInventoryItem {
    public static int FILTER_SIZE = 5;

    public DimensionalQuarryItemFilter(Properties properties) {
        super(properties, FILTER_SIZE, 1);
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
        if (stack.getItem() instanceof DimensionalQuarryItemFilter) {
            return new SimpleMenuProvider(
                    (containerId, playerInventory, player1) -> new DimensionalQuarryItemFilterContainer(
                            containerId,
                            playerInventory,
                            stack
                    ),
                    Component.translatable("menu.cursedcomponents.dimensional_quarry_item_filter.title")
            );
        }

        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.cursedcomponents.dimensional_quarry_item_filter.1"));
        tooltipComponents.add(Component.translatable("tooltip.cursedcomponents.dimensional_quarry_item_filter.2"));


        boolean empty = true;
        for (int i = 0; i < FILTER_SIZE; i++) {
            ItemStack s = this.getStackInSlot(stack, i);
            if (!s.isEmpty()) {
                tooltipComponents.add(Component.literal("  - ").append(s.getDisplayName()));
                empty = false;
            }
        }

        if (empty)
            tooltipComponents.add(Component.translatable("tooltip.cursedcomponents.dimensional_quarry_item_filter.empty"));
    }
}
