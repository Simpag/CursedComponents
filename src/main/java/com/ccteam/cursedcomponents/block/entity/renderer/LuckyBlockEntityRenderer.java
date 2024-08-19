package com.ccteam.cursedcomponents.block.entity.renderer;

import com.ccteam.cursedcomponents.additional.ModAdditionalModels;
import com.ccteam.cursedcomponents.block.entity.custom.LuckyBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

public class LuckyBlockEntityRenderer implements BlockEntityRenderer<LuckyBlockEntity> {

    public LuckyBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(LuckyBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        BakedModel model = Minecraft.getInstance().getModelManager().getModel(ModAdditionalModels.BLOCK_DICE_LOCATION);

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        poseStack.pushPose();
        poseStack.translate(0.5, 0.4, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(blockEntity.getDiceRotation()));

        itemRenderer.renderModelLists(model, ItemStack.EMPTY, packedLight, OverlayTexture.NO_OVERLAY,
                poseStack, bufferSource.getBuffer(RenderType.solid()));

        poseStack.popPose();
    }
}