package com.ccteam.cursedcomponents.block.entity.renderer;

import com.ccteam.cursedcomponents.additional.ModAdditionalModels;
import com.ccteam.cursedcomponents.block.entity.custom.LuckyBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class LuckyBlockEntityRenderer implements BlockEntityRenderer<LuckyBlockEntity> {

    public LuckyBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(LuckyBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        BakedModel model = Minecraft.getInstance().getModelManager().getModel(ModAdditionalModels.BLOCK_DICE_LOCATION);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.4, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(blockEntity.getDiceRotation()));

        renderTintedModelLists(model, ItemStack.EMPTY, packedLight, OverlayTexture.NO_OVERLAY, poseStack,
                bufferSource.getBuffer(RenderType.translucent()),
                blockEntity.getTintColor());

        poseStack.popPose();
    }


    private void renderTintedModelLists(BakedModel model, ItemStack stack,
                                        int combinedLight, int combinedOverlay,
                                        PoseStack poseStack, VertexConsumer buffer,
                                        int tintColor) {
        RandomSource randomsource = RandomSource.create();
        long seed = 42L;

        for (Direction direction : Direction.values()) {
            randomsource.setSeed(seed);
            this.renderQuadList(poseStack, buffer, model.getQuads(null, direction, randomsource), stack, combinedLight, combinedOverlay, tintColor);
        }

        randomsource.setSeed(seed);
        this.renderQuadList(poseStack, buffer, model.getQuads(null, null, randomsource), stack, combinedLight, combinedOverlay, tintColor);
    }

    private void renderQuadList(PoseStack poseStack, VertexConsumer buffer, List<BakedQuad> quads, ItemStack itemStack, int combinedLight, int combinedOverlay, int tintColor) {
        PoseStack.Pose posestack$pose = poseStack.last();

        for (BakedQuad bakedquad : quads) {
            float f = (float) FastColor.ARGB32.alpha(tintColor) / 255.0F;
            float f1 = (float) FastColor.ARGB32.red(tintColor) / 255.0F;
            float f2 = (float) FastColor.ARGB32.green(tintColor) / 255.0F;
            float f3 = (float) FastColor.ARGB32.blue(tintColor) / 255.0F;
            buffer.putBulkData(posestack$pose, bakedquad, f1, f2, f3, f, combinedLight, combinedOverlay, true); // Neo: pass readExistingColor=true
        }
    }
}
