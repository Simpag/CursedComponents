package com.ccteam.cursedcomponents.block.entity.renderer;

import com.ccteam.cursedcomponents.additional.ModAdditionalModels;
import com.ccteam.cursedcomponents.block.entity.custom.LuckyBlockEntity;
import com.ccteam.cursedcomponents.util.RenderUtil;
import com.ccteam.cursedcomponents.util.VoxelShapeUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.joml.Quaternionf;

import java.util.List;

public class LuckyBlockEntityRenderer implements BlockEntityRenderer<LuckyBlockEntity> {

    public LuckyBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(LuckyBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        if (blockEntity.getLevel() == null)
            return;

        BakedModel model = Minecraft.getInstance().getModelManager().getModel(ModAdditionalModels.BLOCK_DICE_LOCATION);

        poseStack.pushPose();

        if (blockEntity.isSpinning()) {
            float rot = blockEntity.getDiceRotation();
            float sin = (float) Math.sin(Math.toRadians(rot)) / 10f;
            float cos = (float) Math.cos(Math.toRadians(rot)) / 10f;
            float sincos = sin * cos * 10f;
            poseStack.scale(1f + sincos, 1f + sincos , 1f + sincos);
            poseStack.translate(cos, sincos, sin);
            poseStack.rotateAround(Axis.YP.rotationDegrees(rot), 0.5f, 0f, 0.5f);
        }

        RenderUtil.renderTintedModelLists(model, RenderUtil.getLightLevel(blockEntity.getLevel(), blockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, poseStack,
                bufferSource.getBuffer(RenderType.TRANSLUCENT),
                RenderType.TRANSLUCENT, blockEntity.getTintColor());

        poseStack.popPose();
    }
}
