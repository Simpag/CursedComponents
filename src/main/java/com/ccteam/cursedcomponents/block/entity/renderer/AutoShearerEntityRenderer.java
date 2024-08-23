package com.ccteam.cursedcomponents.block.entity.renderer;

import com.ccteam.cursedcomponents.block.entity.custom.AutoShearerEntity;
import com.ccteam.cursedcomponents.util.RenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AutoShearerEntityRenderer implements BlockEntityRenderer<AutoShearerEntity> {

    public AutoShearerEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(AutoShearerEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack shears = new ItemStack(Items.SHEARS);

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.4f, 0.5);
        poseStack.scale(0.8f, 0.8f, 0.8f);

        itemRenderer.renderStatic(shears, ItemDisplayContext.FIXED, RenderUtil.getLightLevel(blockEntity.getLevel(), blockEntity.getBlockPos()),
                OverlayTexture.NO_OVERLAY, poseStack, bufferSource, blockEntity.getLevel(), 1);

        poseStack.popPose();
    }
}
