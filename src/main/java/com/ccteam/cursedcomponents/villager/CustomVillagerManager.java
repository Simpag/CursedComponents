package com.ccteam.cursedcomponents.villager;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

public class CustomVillagerManager {
    public CustomVillagerManager() {
    }

    public static Villager createLuckyVillager(Level world) {
        Villager villager = EntityType.VILLAGER.create(world);
        if (villager == null) {
            return null;
        }
        villager.setVillagerData(villager.getVillagerData().setProfession(VillagerProfession.MASON));
        villager.setVillagerXp(10000);

        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD.asItem(), 1);

        EnchantmentHelper.enchantItem(world.random, sword, 30, world.registryAccess(),
                world.registryAccess().registryOrThrow(Registries.ENCHANTMENT)
                        .getTag(EnchantmentTags.DAMAGE_EXCLUSIVE));

        MerchantOffer offer = new MerchantOffer(
                new ItemCost(Items.WHEAT, 16),
                sword,
                2, 9, 0.05f
        );

        MerchantOffers offers = new MerchantOffers();
        offers.add(offer);
        villager.setOffers(offers);
        return villager;
    }
}
