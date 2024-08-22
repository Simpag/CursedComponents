package com.ccteam.cursedcomponents.block.entity.renderer;

import com.ccteam.cursedcomponents.additional.ModAdditionalModels;
import com.ccteam.cursedcomponents.block.ModBlocks;
import com.ccteam.cursedcomponents.block.entity.custom.DimensionalQuarryEntity;
import com.ccteam.cursedcomponents.util.RenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class DimensionalQuarryEntityRenderer implements BlockEntityRenderer<DimensionalQuarryEntity> {
    private static final Vector2f overworld_scan_offset = new Vector2f(0.21F, 0.82F);
    private static final Vector2f nether_scan_offset = new Vector2f(0.21F, 0.578F);
    private static final Vector2f end_scan_offset = new Vector2f(0.21F, 0.635F);

    public DimensionalQuarryEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(DimensionalQuarryEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack stack = blockEntity.getMiniChunkInSlot();

        // No need to render if it does not have anything
        if (stack.isEmpty() || blockEntity.getLevel() == null)
            return;

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.7f, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(blockEntity.getMiniChunkRotation()));

        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, RenderUtil.getLightLevel(blockEntity.getLevel(), blockEntity.getBlockPos()),
                OverlayTexture.NO_OVERLAY, poseStack, bufferSource, blockEntity.getLevel(), 1);

        poseStack.popPose();

        if (blockEntity.getCurrentYLevel() != null && blockEntity.getMinCurrentYLevel() != null) {
            poseStack.pushPose();

            ItemStack miniChunk = blockEntity.getMiniChunkInSlot();
            float minY = blockEntity.getMinCurrentYLevel();
            float maxY = blockEntity.getMaxCurrentYLevel();
            float curY = blockEntity.getCurrentYLevel();
            float y = 0;

            if (miniChunk.is(ModBlocks.MINI_CHUNK_OVERWORLD.asItem())) {
                y = overworld_scan_offset.x + (overworld_scan_offset.y - overworld_scan_offset.x) * (curY - minY) / (maxY - minY);
            } else if (miniChunk.is(ModBlocks.MINI_CHUNK_NETHER.asItem())) {
                y = nether_scan_offset.x + (nether_scan_offset.y - nether_scan_offset.x) * (curY - minY) / (maxY - minY);
            } else if (miniChunk.is(ModBlocks.MINI_CHUNK_END.asItem())) {
                y = end_scan_offset.x + (end_scan_offset.y - end_scan_offset.x) * (curY - minY) / (maxY - minY);
            }

            poseStack.translate(0, y, 0);
            BakedModel model = Minecraft.getInstance().getModelManager().getModel(ModAdditionalModels.DIMENSIONAL_QUARRY_QUAD_LOCATION);
            RenderUtil.renderTintedModelLists(model, RenderUtil.getLightLevel(blockEntity.getLevel(), blockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, poseStack,
                    bufferSource.getBuffer(RenderType.TRANSLUCENT),
                    RenderType.TRANSLUCENT, FastColor.ARGB32.color(255, 0xFF0000));

            poseStack.popPose();
        }
    }
}
