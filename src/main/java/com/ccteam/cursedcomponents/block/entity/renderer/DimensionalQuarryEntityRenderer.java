package com.ccteam.cursedcomponents.block.entity.renderer;

import com.ccteam.cursedcomponents.block.entity.custom.DimensionalQuarryEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class DimensionalQuarryEntityRenderer implements BlockEntityRenderer<DimensionalQuarryEntity> {
    public DimensionalQuarryEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(DimensionalQuarryEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack stack = blockEntity.getMiniChunkInSlot();

        // No need to render if it does not have anything
        if (stack.isEmpty())
            return;

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.7f, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(blockEntity.getMiniChunkRotation()));

        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, getLightLevel(blockEntity.getLevel(), blockEntity.getBlockPos()),
                OverlayTexture.NO_OVERLAY, poseStack, bufferSource, blockEntity.getLevel(), 1);

        poseStack.popPose();
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}
