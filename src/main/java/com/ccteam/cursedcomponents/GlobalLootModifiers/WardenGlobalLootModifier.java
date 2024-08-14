package com.ccteam.cursedcomponents.GlobalLootModifiers;

import com.ccteam.cursedcomponents.item.ModItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class WardenGlobalLootModifier extends LootModifier {
    public static final MapCodec<WardenGlobalLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            LootModifier.codecStart(inst).and(inst.group(
                    Codec.INT.fieldOf("min_items").forGetter(e -> e.min_items),
                    Codec.INT.fieldOf("max_items").forGetter(e -> e.max_items)
            )).apply(inst, WardenGlobalLootModifier::new));

    private final int min_items;
    private final int max_items;

    public WardenGlobalLootModifier(LootItemCondition[] conditions, int min_items, int max_items) {
        super(conditions);
        this.min_items = min_items;
        this.max_items = max_items;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        int glue_drop_count = context.getRandom().nextIntBetweenInclusive(min_items, max_items);
        generatedLoot.add(ModItems.WARDEN_GLUE.toStack(glue_drop_count));
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
