package com.ccteam.cursedcomponents.entity.client;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.entity.custom.LuckyParrot;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class LuckyParrotRenderer extends MobRenderer<LuckyParrot, LuckyParrotModel> {

    public static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath(CursedComponentsMod.MOD_ID, "textures/entity/lucky_parrot/lucky_parrot.png");

    public LuckyParrotRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new LuckyParrotModel(pContext.bakeLayer(ModModelLayers.LUCKY_PARROT)), 0.2f);
    }

    @Override
    public ResourceLocation getTextureLocation(LuckyParrot pEntity) {
        return LOCATION;
    }

    @Override
    public void render(LuckyParrot pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack,
                       MultiBufferSource pBuffer, int pPackedLight) {
        if(pEntity.isBaby()) {
            pPoseStack.scale(0.45f, 0.45f, 0.45f);
        }

        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }
}
