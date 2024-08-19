package com.ccteam.cursedcomponents.additional;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;

public class ModAdditionalModels {
    public static final ModelResourceLocation BLOCK_DICE_LOCATION = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(
            CursedComponentsMod.MOD_ID, "block/dice"));

    public static void register(ModelEvent.RegisterAdditional event) {
        event.register(BLOCK_DICE_LOCATION);
    }
}
